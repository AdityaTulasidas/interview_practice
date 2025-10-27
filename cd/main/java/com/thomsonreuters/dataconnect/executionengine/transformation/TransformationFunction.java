package com.thomsonreuters.dataconnect.executionengine.transformation;

import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.thomsonreuters.dataconnect.executionengine.data.DataRow;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;

import java.util.List;

public interface TransformationFunction {



    public void initialize(Transformations config) throws DataSyncJobException;

    // perform trnasformation to the input object and return it as output object
    public DataRow execute(DataRow input, ExecutionContext context) throws DataSyncJobException;


    void Validate(DataRow input, ExecutionContext context);



    // providing cleanup hook point to transformation function
    void cleanup();
}
