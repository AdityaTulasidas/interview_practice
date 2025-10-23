package com.aditya.dataconnect.executionengine.listeners;

import com.rabbitmq.client.Channel;
import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.aditya.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.aditya.dataconnect.executionengine.exceptionhandler.ListenerException;
import com.aditya.dataconnect.executionengine.model.pojo.DatasyncMessage;
import com.aditya.dataconnect.executionengine.services.DataSyncExecutionEngine;
import com.aditya.dataconnect.executionengine.utils.ExecutionContextUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
public class JobListener {

    public static final String ERROR_CODE = "INTERNAL_SERVER_ERROR";

    @Autowired
    private ExecutionContextUtil executionContextUtil;

    @Autowired
    private DataSyncExecutionEngine dataSyncExecutionEngine;

    // Log when the listener is initializedtruct
    @PostConstruct
    public void init() {
        log.info("JobListener initialized and ready to consume messages.");
    }

    @RabbitListener(id = "jobListener", queues = "${spring.rabbitmq.connections.data-sync.source.job.queue.name}", containerFactory = "rabbitJobListenerContainerFactory")
    public void listenAndConsume(DatasyncMessage dataSyncMessage, Message message, Channel channel) throws IOException {
        try {
            log.debug("MsgRecvdTime: {}, JobListener_Thread: {}, ConsumerTag: {}, JobExecutionId: {}",
                    LocalDateTime.now(),
                    Thread.currentThread().getName(),
                    message.getMessageProperties().getConsumerTag(),
                    dataSyncMessage.getHeader().getJobExecId().toString());

            log.info("In JobListener listenAndConsume {}", dataSyncMessage.getHeader().getJobExecId().toString());

            ExecutionContext ctx = executionContextUtil.buildExecutionContext(dataSyncMessage);
            if (dataSyncMessage == null || dataSyncMessage.getHeader() == null) {
                throw new DataSyncJobException("Invalid datasync message", "BAD_REQUEST");
            }
            MDC.put("job.id", dataSyncMessage.getHeader().getJobId().toString());
            MDC.put("job.exec.id",  dataSyncMessage.getHeader().getJobExecId().toString());
            MDC.put("regional.execution.id",  dataSyncMessage.getHeader().getRegionalExecId().toString());
            StopWatch stopWatch =new StopWatch();
            stopWatch.start();
            dataSyncExecutionEngine.executeTask(ctx);
            stopWatch.stop();
            log.info("Source Task executed {} ms", stopWatch.getTotalTimeMillis());
            MDC.clear();
            // acknowledge a single message at once, if "true" it will acknowledge multiple messages.
        } catch (Exception e) {
            log.error("Error while consuming the queue message from the source queue:{}", message.getMessageProperties().getConsumerQueue(), e);
            // reject a single message at once and requeue the message.
            // if 1st boolean is set to false then single message are acknowledged
            // if 2nd boolean is set to true then failed message is requeued again.
            throw new ListenerException("Failed to consume and process the message with error : "+e.getMessage(), ERROR_CODE);
        } finally {
            // Acknowledge the message
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}