

package com.thomsonreuters.dataconnect.executionengine.transformation;

import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.thomsonreuters.dataconnect.common.executioncontext.RegionalJobContext;
import com.thomsonreuters.dataconnect.executionengine.data.DataRow;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ReplaceRegionalTenant implements TransformationFunction {
     
    private static final String TENANT_ID_FIELD = "tenant_id";
    private static final String SOURCE_VALUE_EXP = "$ctx.job.source_tenant";
    private static final String TARGET_Value_VALUE_EXP = "$ctx.job.target_tenant";

    private String sourceValue;
    private String targetValue;
    private boolean isLoaded;



    @Override
    public void initialize(Transformations config) throws DataSyncJobException {

    }

    @Override
    public DataRow execute(DataRow input, ExecutionContext context) throws DataSyncJobException {

        if(!input.getRow().containsKey(TENANT_ID_FIELD)) {
            log.warn("Input DataRow does not contain the field: {}", TENANT_ID_FIELD);
        }
        else {
            if (!isLoaded) {
                loadValues(context);
                this.isLoaded = true;
            }
            String tenantId = input.getRow().get(TENANT_ID_FIELD).toString();
            input.getRow().put(TENANT_ID_FIELD, this.targetValue);
        }
        return input;
    }

    @Override
    public void Validate(DataRow input, ExecutionContext context) {

    }



    private void loadValues(ExecutionContext context) {

        this.sourceValue = context.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT).getValue(RegionalJobContext.CUSTOMER_TENANT_ID).toString();
        this.targetValue = context.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT).getValue(RegionalJobContext.REGIONAL_TENANT_ID).toString();
    }





    @Override
    public void cleanup() {

    }
}
