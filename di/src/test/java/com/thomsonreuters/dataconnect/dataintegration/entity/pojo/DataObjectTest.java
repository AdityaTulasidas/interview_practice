package com.thomsonreuters.dataconnect.dataintegration.entity.pojo;

import com.thomsonreuters.dataconnect.dataintegration.dto.MetaObjectAttributeDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.MetaObjectDTO;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.DataType;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.DataCollectionObject;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.DataObject;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataObjectTest {

    @Test
    void shouldSetAndGetValueSuccessfully_WhenKeyAndValueAreProvided() {
        DataObject dataObject = new DataObject();
        dataObject.setValue("key1", "value1");
        assertEquals("value1", dataObject.getValue("key1"));
    }

    @Test
    void shouldSetAndGetIntegerSuccessfully_WhenKeyAndIntegerValueAreProvided() {
        DataObject dataObject = new DataObject();
        dataObject.setInteger("key2", 42);
        assertEquals(42, dataObject.getInteger("key2"));
    }

    @Test
    void shouldSetAndGetDateTimeSuccessfully_WhenKeyAndDateTimeValueAreProvided() {
        DataObject dataObject = new DataObject();
        LocalDateTime now = LocalDateTime.now();
        dataObject.setDateTime("key3", now);
        assertEquals(now, dataObject.getDateTime("key3"));
    }

    @Test
    void shouldCreateDataObjectSuccessfully_WhenMetaObjectAndResultSetAreProvided() throws SQLException {
        MetaObjectDTO metaObjectDTO = mock(MetaObjectDTO.class);
        MetaObjectAttributeDTO attributeDTO = mock(MetaObjectAttributeDTO.class);
        ResultSet resultSet = mock(ResultSet.class);

        Map<String, MetaObjectAttributeDTO> attributes = new HashMap<>();
        attributes.put("key1", attributeDTO);

        when(metaObjectDTO.getAttributes()).thenReturn(attributes);
        when(attributeDTO.getDataType()).thenReturn(DataType.STRING);
        when(attributeDTO.getDbColumn()).thenReturn("column1");
        when(resultSet.getString("column1")).thenReturn("value1");

        DataObject dataObject = DataObject.createObject(metaObjectDTO, resultSet);

        assertEquals("value1", dataObject.getValue("column1"));
    }

    @Test
    void shouldReturnNull_WhenChildObjectIsNotSet() {
        DataObject dataObject = new DataObject();
        DataCollectionObject child = new DataCollectionObject();
        dataObject.setValue("child1", child);
        assertNull(dataObject.getChildObjects("child1"));
    }
}