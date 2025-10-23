package com.aditya.dataconnect.executionengine.listeners;

import com.rabbitmq.client.Channel;
import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.thomsonreuters.dataconnect.common.logging.LogClient;
import com.aditya.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.aditya.dataconnect.executionengine.exceptionhandler.ListenerException;
import com.aditya.dataconnect.executionengine.model.entity.JobExecutionLog;
import com.aditya.dataconnect.executionengine.model.pojo.DatasyncMessage;
import com.aditya.dataconnect.executionengine.model.pojo.Header;
import com.aditya.dataconnect.executionengine.services.DataSyncExecutionEngine;
import com.aditya.dataconnect.executionengine.utils.ExecutionContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.UUID;

import static com.aditya.dataconnect.executionengine.constant.Constants.ACCEPTED;

@Slf4j
@Service
public class FileListener {

    public static final String ERROR_CODE = "INTERNAL_SERVER_ERROR";

    @Autowired
    private ExecutionContextUtil executionContextUtil;
    @Autowired
    private DataSyncExecutionEngine dataSyncExecutionEngine;
    @Autowired
    private LogClient logClient;


    @RabbitListener(id = "fileListener", queues = "${spring.rabbitmq.connections.data-sync.source.fileSource.queue.name}", containerFactory = "rabbitFileListenerContainerFactory")
    public void listenAndConsume(DatasyncMessage dataSyncMessage, Message message, Channel channel) throws IOException {
        try {
            log.debug("FileListener_Thread: {}, ConsumerTag: {}, JobExecutionId: {}", Thread.currentThread().getName(),
                    message.getMessageProperties().getConsumerTag(), dataSyncMessage.getHeader().getJobExecId().toString());
            Header header = dataSyncMessage.getHeader();
            executionContextUtil.validateHeader(header);
            String jobId = header.getJobId().toString();
            String execId = header.getJobExecId().toString();
            JobExecutionLog jobExecutionLog = dataSyncExecutionEngine.saveJobExecutionLog(UUID.fromString(jobId),UUID.fromString(execId), ACCEPTED);
            log.info("FileListener Target region receiving queue: {}", execId);
            header.setRegionalExecId(jobExecutionLog.getId());
            ExecutionContext ctx = executionContextUtil.buildExecutionContext(dataSyncMessage);
            if (dataSyncMessage == null || dataSyncMessage.getHeader() == null) {
                throw new DataSyncJobException("Invalid datasync message", "BAD_REQUEST");
            }
            MDC.put("job.id", jobId);
            MDC.put("job.exec.id",  execId);
            MDC.put("regional.execution.id",  header.getRegionalExecId().toString());
            StopWatch stopWatch =new StopWatch();
            stopWatch.start();
            //logClient.jobAccepted(ctx);
            dataSyncExecutionEngine.executeTask(ctx);
            stopWatch.stop();
            log.info("Target Task executed {} ms", stopWatch.getTotalTimeMillis());
            MDC.clear();
        } catch (Exception e) {
            log.error("Error while consuming the queue message from the queue: {}", message.getMessageProperties().getConsumerQueue(), e);
            // reject a single message at once and requeue the message.
            // if 1st boolean is set to false then single message are acknowledged
            // if 2nd boolean is set to true then failed message is requeued again.
            throw new ListenerException("Failed to consume and process the message with error : "+e.getMessage(), ERROR_CODE);
        }  finally {
            // Acknowledge the message
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}