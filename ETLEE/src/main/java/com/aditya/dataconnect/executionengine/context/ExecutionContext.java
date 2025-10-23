package com.aditya.dataconnect.executionengine.context;

import com.aditya.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExecutionContext {
    public static final String GLOBAL_CONTEXT = "global";
    public static final String REGIONAL_JOB_CONTEXT = "job";
    public static final String IN_ADAPTER_CONTEXT = "inadp";
    public static final String OUT_ADAPTER_CONTEXT = "outadp";
    public static final String TRANSFORMATION_CONTEXT = "transfrom";
    public static final String DATA_SYNC_ACTIVITY_CONTEXT = "datasyncactivity";
    private GlobalContext globalCtx;
    private RegionalJobContext regionalJobCtx;
    private AdapterContext inAdapterCtx;
    private AdapterContext outAdapterCtx;
    private TransformationContext transformationCtx;

    public ExecutionContextBase getContextByName(String name) {
        if (name == null) {
            return null;
        } else {
            switch (name.toLowerCase()) {
                case "global":
                    if (this.globalCtx != null) {
                        return this.globalCtx;
                    }
                case "job":
                    if (this.regionalJobCtx != null) {
                        return this.regionalJobCtx;
                    }
                case "inadp":
                    if (this.inAdapterCtx != null) {
                        return this.inAdapterCtx;
                    }
                case "outadp":
                    if (this.outAdapterCtx != null) {
                        return this.outAdapterCtx;
                    }
                case "transfrom":
                    if (this.transformationCtx != null) {
                        return this.transformationCtx;
                    }
               /* case "datasyncactivity":
                    if (this.dataSyncActivityCtx != null) {
                        return this.dataSyncActivityCtx;
                    }*/
                default:
                    return null;
            }
        }
    }

    public void setContextByName(String name, ExecutionContextBase ctx) throws DataSyncJobException {
        if (name == null) {
            throw new DataSyncJobException("Context name cannot be null","BAD_REQUEST");
        } else if (ctx == null) {
            throw new DataSyncJobException("Context instance cannot be null for name:: " + name,"BAD_REQUEST");
        } else {
            switch (name.toLowerCase()) {
                case "global":
                    if (!(ctx instanceof GlobalContext)) {
                        throw new DataSyncJobException("Invalid context instance provided. Expected:: GlobalContext, Received:: " + ctx.getClass().getName(),"BAD_REQUEST");
                    }

                    this.globalCtx = (GlobalContext)ctx;
                    break;
                case "job":
                    if (!(ctx instanceof RegionalJobContext)) {
                        throw new DataSyncJobException("Invalid context instance provided. Expected:: RegionalJobContext, Received:: " + ctx.getClass().getName(),"BAD_REQUEST");
                    }

                    this.regionalJobCtx = (RegionalJobContext)ctx;
                    break;
                case "inadp":
                    if (!(ctx instanceof AdapterContext)) {
                        throw new DataSyncJobException("Invalid context instance provided. Expected:: Input AdapterContext, Received:: " + ctx.getClass().getName(),"BAD_REQUEST");
                    }

                    this.inAdapterCtx = (AdapterContext)ctx;
                    break;
                case "outadp":
                    if (!(ctx instanceof AdapterContext)) {
                        throw new DataSyncJobException("Invalid context instance provided. Expected:: Output AdapterContext, Received:: " + ctx.getClass().getName(),"BAD_REQUEST");
                    }

                    this.outAdapterCtx = (AdapterContext)ctx;
                    break;
                case "transfrom":
                    if (!(ctx instanceof TransformationContext)) {
                        throw new DataSyncJobException("Invalid context instance provided. Expected:: TransformationContext, Received:: " + ctx.getClass().getName(),"BAD_REQUEST");
                    }

                    this.transformationCtx = (TransformationContext)ctx;
                    break;
                /*case "datasyncactivity":
                    if (!(ctx instanceof DataSyncActivityContext)) {
                        throw new DataSyncJobException("Invalid context instance provided. Expected:: DataSyncActivityContext, Received:: " + ctx.getClass().getName(),"BAD_REQUEST");
                    }

                    this.dataSyncActivityCtx = (DataSyncActivityContext)ctx;
                    break;*/
                default:
                    throw new DataSyncJobException("Unknown context name provided to get context instance:: " + name,"BAD_REQUEST");
            }

        }
    }

    public ExecutionContext() {
    }
}