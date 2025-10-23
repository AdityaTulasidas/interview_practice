package com.thomsonreuters.dataconnect.executionengine.datasyncactivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.thomsonreuters.dataconnect.executionengine.data.DataSetCollection;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;

public interface DataSyncActivity {

    void initialize(ExecutionContext ctx) throws DataSyncJobException;

    // perform DatSync Activity to the input object and return it as output object
    DataSetCollection execute(DataSetCollection input,ExecutionContext context) throws DataSyncJobException, JsonProcessingException;


    void validate(DataSetCollection input);



    // providing cleanup hook point to transformation function
    void cleanup();
}
