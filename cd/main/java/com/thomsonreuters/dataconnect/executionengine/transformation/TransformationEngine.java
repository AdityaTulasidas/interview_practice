
package com.thomsonreuters.dataconnect.executionengine.transformation;


import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.thomsonreuters.dataconnect.executionengine.data.DataRow;
import com.thomsonreuters.dataconnect.executionengine.data.DataSet;
import com.thomsonreuters.dataconnect.executionengine.data.DataSetCollection;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.DataObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor(force = true)
@Service
@Slf4j
public class TransformationEngine {


    private final List<TransformationFunction> funcList = new ArrayList<>();

    private final TransformationFunctionFactory transformFunctionFactory;

    private List<Transformations> transformations;

    public void initialize(List<Transformations> transformations) throws DataSyncJobException {
        // Initialization logic if needed
        if(transformations != null ) {

            this.transformations = transformations;
            // Initialize TransformationFunction objects and add them to funcList
            for (Transformations config : this.transformations) {
                TransformationFunction func = TransformationFunctionFactory.getFunction(config.getType(), config.getFuncName());
                func.initialize(config);
                this.funcList.add(func);
            }
        }
    }

    public DataSetCollection transform(DataSetCollection dataSetCollection, ExecutionContext ctx) throws DataSyncJobException {
        if (this.funcList.isEmpty()) {
            return  dataSetCollection;
        }
        DataSetCollection transformedDataSetCollection = new DataSetCollection();
        List<DataSet> transformedDataSets = new ArrayList<>();
        for (DataSet dataSet : dataSetCollection.getDataSets()) {
            List<DataRow> dataRows = new ArrayList<>();
            if(dataSet.getDataRows()!= null) {
                for (DataRow dataRow : dataSet.getDataRows()) {
                    DataRow transformedDataRow = transformDataRow(dataRow, ctx);
                    dataRows.add(transformedDataRow);
                    log.info("Transformed DataRow: {}", transformedDataRow);
                }
            }
            log.info("Transformed {} rows in DataSet: {}", dataRows.size(), dataSet.getMetaObject().getDbTable());
            dataSet.setDataRows(dataRows);
            transformedDataSets.add(dataSet);
        }

        transformedDataSetCollection.setDataSets(transformedDataSets);
        return transformedDataSetCollection;
    }

    public DataRow transformDataRow(DataRow input, ExecutionContext execContext) throws DataSyncJobException {

        if (input == null || this.funcList.isEmpty()) {
            return input;
        }
        for (TransformationFunction func : this.funcList) {
            input = func.execute(input, execContext);
        }

        return input;
    }

    public DataObject validate(DataObject input, ExecutionContext execContext) {

        // Validate the input DataObject using the transformation functions
        return null;

    }


    public void cleanUp() {


    }
}