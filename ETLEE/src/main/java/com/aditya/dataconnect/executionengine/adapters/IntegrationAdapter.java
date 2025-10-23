package com.aditya.dataconnect.executionengine.adapters;

import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.aditya.dataconnect.executionengine.data.DataSetCollection;
import com.aditya.dataconnect.executionengine.exceptionhandler.DataSyncJobException;

public interface IntegrationAdapter {

    // Need to be used to test/validate the configuraiton of DataSync Task i.e. without synchronizing the actual data. Kind of simulation
    void validate();

    DataSetCollection readData(ExecutionContext ctx) throws DataSyncJobException;

    // Write data to the transfer channel if configured in source leg OR to dattransfer channel if configured in target leg
    void writeData(DataSetCollection data, ExecutionContext ctx) throws DataSyncJobException;
    // Finalizes the integration adaptor, releasing any allocated resources.

    void cleanUp();
}
