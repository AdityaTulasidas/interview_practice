package com.aditya.ETLExecutionEngine.adaptors;

import com.aditya.ETLExecutionEngine.context.ExecutionContext;
import com.aditya.ETLExecutionEngine.data.DataSetCollection;
import com.aditya.ETLExecutionEngine.exception.DataSyncJobException;

public interface IntegrationAdapter {

    // Initializes the integration adaptor with the provided execution context
    void initialize(ExecutionContext ctx);

    // Need to be used to test/validate the configuraiton of DataSync Task i.e. without synchronizing the actual data. Kind of simulation
    void validate();

    // Read data from data source if configured in source leg OR from transfer channel if configured in target leg
    DataSetCollection readData() throws DataSyncJobException;

    // Write data to the transfer channel if configured in source leg OR to dattransfer channel if configured in target leg
    void writeData(DataSetCollection data) throws DataSyncJobException;
    // Finalizes the integration adaptor, releasing any allocated resources.

    void cleanUp();
}
 