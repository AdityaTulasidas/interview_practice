package com.thomsonreuters.dataconnect.executionengine.adapters;

import com.thomsonreuters.dataconnect.common.executioncontext.AdapterContext;
import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.thomsonreuters.dataconnect.executionengine.adapters.impl.DataStreamAdapter;
import com.thomsonreuters.dataconnect.executionengine.adapters.impl.DatabaseAdapter;
import com.thomsonreuters.dataconnect.executionengine.adapters.impl.FileAdapter;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.thomsonreuters.dataconnect.executionengine.constant.Constants.BAD_REQUEST;

@Slf4j
@Component
public class AdapterFactory {
    @Autowired
    private DataStreamAdapter dataStreamAdapter;
    @Autowired
    private DatabaseAdapter databaseAdapter;
    @Autowired
    private FileAdapter fileAdapter;

    public static final String DATASTREAM_ADAPTOR_ID = "STREAMADAPTOR";
    public static final String DATABASE_ADAPTOR_ID = "DATABASEADAPTOR";
    public static final String FILE_ADAPTOR_ID = "FILEADAPTOR";

    public IntegrationAdapter getAdapter(ExecutionContext ctx, String adapterType) throws DataSyncJobException { //adapterType - inadp, outadp
        AdapterContext adpCtx = (AdapterContext) ctx.getContextByName(adapterType);
        switch(adpCtx.getValue(AdapterContext.ADOPTER_ID).toString()){
            case DATASTREAM_ADAPTOR_ID:
                return dataStreamAdapter;
            case DATABASE_ADAPTOR_ID:
                return databaseAdapter;
            case FILE_ADAPTOR_ID:
                return fileAdapter;
            default:
                log.error("No such adaptor {}",adpCtx.getValue(AdapterContext.ADOPTER_ID).toString());
                throw new DataSyncJobException("No such adaptor",BAD_REQUEST);
        }
    }


}
