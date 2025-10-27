package com.thomsonreuters.dataconnect.executionengine.transformation;

import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.thomsonreuters.dataconnect.executionengine.data.DataRow;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ReplaceValue implements TransformationFunction {
    
    private static final String FIELD_NAME_PARAM = "field_name";
    private static final String SOURCE_VALUE_PARAM = "source_value";
    private static final String TARGET_VALUE_PARAM = "target_value";
    private static final String DATA_TYPE = "data_type";

    private String fieldName;
    private Object srcValue;
    private Object tgtValue;
    private DataType dtType;


    @Override
    public void initialize(Transformations config) throws DataSyncJobException {
            //this.dtType= DataType.valueOf(config.getValue(this.fieldName));
            this.fieldName = config.getValue(FIELD_NAME_PARAM);
            this.srcValue = config.getValue(SOURCE_VALUE_PARAM);
            this.tgtValue = config.getValue(TARGET_VALUE_PARAM);
    }


    @Override
    public void Validate(DataRow input, ExecutionContext context) {

    }

    @Override
    public DataRow execute(DataRow input, ExecutionContext context) throws DataSyncJobException {

        if(input != null && input.getRow().containsKey(this.fieldName)) {
            Object srcDbVal = input.getRow().get(this.fieldName);

            if (srcDbVal!=null && !this.srcValue.equals(srcDbVal)) {
                log.error("Field '{}' value '{}' does not match source value '{}'. No replacement will be made.",
                        this.fieldName, srcDbVal, this.srcValue);
            }else {
                input.getRow().put(this.fieldName, this.tgtValue);
            }

        }
        return input;
    }


    private Object convertToTargetDataType(DataType dtType, Object tgtValue) throws DataSyncJobException {
       if (dtType == DataType.STRING || dtType == DataType.TEXT) {
           return String.valueOf(tgtValue);
       } else if (dtType == DataType.INTEGER) {
           return Integer.parseInt(String.valueOf(tgtValue));
       } else if (dtType == DataType.LONG) {
           return Double.parseDouble(String.valueOf(tgtValue));
       } else if (dtType == DataType.BOOLEAN) {
           return Boolean.parseBoolean(String.valueOf(tgtValue));
       } else if (dtType == DataType.DATETIME) {
           return LocalDateTime.parse(String.valueOf(tgtValue));
       } else if (dtType == DataType.UUID) {
           return java.util.UUID.fromString(String.valueOf(tgtValue));
       } else if(dtType == DataType.DATE){
              return java.sql.Date.valueOf(String.valueOf(tgtValue));
       }
       else {
           throw new DataSyncJobException("Unsupported data type: " + dtType,
                   "Cannot convert value: " + tgtValue);
       }
    }

    @Override
    public void cleanup() {

    }


}
