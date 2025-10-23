package com.aditya.dataconnect.executionengine.transformation;

import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.aditya.dataconnect.executionengine.data.DataRow;
import com.aditya.dataconnect.executionengine.exceptionhandler.DataSyncJobException;

public interface TransformationFunction {



    public void initialize(Transformations config) throws DataSyncJobException;

    // perform trnasformation to the input object and return it as output object
    public DataRow execute(DataRow input, ExecutionContext context) throws DataSyncJobException;


    void Validate(DataRow input, ExecutionContext context);



    // providing cleanup hook point to transformation function
    void cleanup();
}
