package com.aditya.ETLExecutionEngine.adaptors;

import com.aditya.ETLExecutionEngine.context.ExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataStreamAdapter  implements IntegrationAdapter {

    private ExecutionContext ctx;

    @Autowired
    CommonAdaptersUtil commonAdaptersUtil;

    public static final String DATA_UNIT = "data_unit";

    @Autowired
    @Qualifier("targetsRabbitMQTemplates")
    Map<String, RabbitTemplate> targetsRabbitMQTemplates;

    @Autowired
    @Qualifier("targetsRabbitMQExchanges")
    Map<String, String> targetsRabbitMQExchanges;

    @Autowired
    @Qualifier("targetsRabbitMQRoutingKeys")
    Map<String, String> targetsRabbitMQRoutingKeys;

    private DataUnit data;

    @Override
    public void initialize(ExecutionContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void validate() {
    }

    @Override
    public DataSetCollection readData() throws DataSyncJobException {
        RegionalJobContext regionalJobContext = (RegionalJobContext) ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT);
        UUID jobId = regionalJobContext.getValue(RegionalJobContext.JOB_ID);
        AdapterContext adapterContext = (AdapterContext) ctx.getContextByName(ExecutionContext.IN_ADAPTER_CONTEXT);
        DataUnit data = adapterContext.getValue(DATA_UNIT);
        if (data == null || !(data.getContent() instanceof DataSetCollection)) {
            throw new DataSyncJobException("DataUnit is not set in the AdapterContext."+ jobId.toString(),"BAD_REQUEST");
        }
        return (DataSetCollection) data.getContent();
    }

    @Override
    public void writeData(DataSetCollection dataSetCollection) {
        GlobalContext globalContext = (GlobalContext) ctx.getContextByName(ExecutionContext.GLOBAL_CONTEXT);

        MessagingContext messagingContext = (MessagingContext) globalContext.getValue(GlobalContext.MESSAGING_CONTEXT);
        String exchange = messagingContext.getValue(MessagingContext.EXCHANGE);

        RegionalJobContext regionalJobContext = (RegionalJobContext) ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT);

        String targetRegions = regionalJobContext.getValue(RegionalJobContext.TARGET_REGIONS).toString();
        if (StringUtils.isNotBlank(targetRegions)) {
            Header header = commonAdaptersUtil.generateHeader(regionalJobContext);
            DataUnit dataUnit = new DataUnit();
            dataUnit.setContent(dataSetCollection);

            String[] targets = targetRegions.split(",");
            for (String target : targets) {
                target = target.trim();
                DatasyncMessage message = new DatasyncMessage();
                message.setHeader(header);
                message.setData(dataUnit);
                try {
                    RabbitTemplate targetRabbitMQTemplate = targetsRabbitMQTemplates.get(target);
                    targetRabbitMQTemplate.convertAndSend(targetsRabbitMQExchanges.get(target),
                            targetsRabbitMQRoutingKeys.get(target),
                            message);
                } catch (Exception e) {
                    log.error("Failed to send message to target:: {} for regional job:: {} with error:: {}",
                            target, regionalJobContext.getValue(RegionalJobContext.REGIONAL_JOB_NAME), e.getMessage(), e);
                }
            }
        } else {
            throw new IllegalStateException("No target regions specified in the Regional Job:: " +
                    regionalJobContext.getValue(RegionalJobContext.REGIONAL_JOB_NAME));
        }
    }

    @Override
    public void cleanUp() {

    }
}