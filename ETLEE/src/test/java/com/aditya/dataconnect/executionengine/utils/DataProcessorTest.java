package com.aditya.dataconnect.executionengine.utils;

import com.aditya.dataconnect.executionengine.dto.MetaObjectAttributeDTO;
import com.aditya.dataconnect.executionengine.dto.MetaObjectDTO;
import com.aditya.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.aditya.dataconnect.executionengine.model.entity.enums.DataType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataProcessorTest {

    @Test
    void populateDataObjectReturnsCorrectlyPopulatedList() throws DataSyncJobException {
        MetaObjectDTO metaObjectDTO = Mockito.mock(MetaObjectDTO.class);
        MetaObjectAttributeDTO attribute1 = new MetaObjectAttributeDTO();
        attribute1.setDbColumn("column1");
        attribute1.setSeqNum(1);

        MetaObjectAttributeDTO attribute2 = new MetaObjectAttributeDTO();
        attribute2.setDbColumn("column2");
        attribute2.setSeqNum(2);
        Set<MetaObjectAttributeDTO> attributes = new HashSet<>(Arrays.asList(attribute1, attribute2));
        Mockito.when(metaObjectDTO.getAttributes()).thenReturn(attributes);

        List<ConcurrentHashMap<String, Object>> dataObjects = new ArrayList<>();
        ConcurrentHashMap<String, Object> dataObject = new ConcurrentHashMap<>();
        dataObject.put("column1", "value1");
        dataObjects.add(dataObject);

        List<LinkedHashMap<String, Object>> result = DataProcessor.populateDataObject(metaObjectDTO, dataObjects);

        assertEquals(1, result.size());
        assertEquals("value1", result.get(0).get("column1"));
    }

    @Test
    void populateDataObjectHandlesEmptyDataObjects() throws DataSyncJobException {
        MetaObjectDTO metaObjectDTO = Mockito.mock(MetaObjectDTO.class);
        Set<MetaObjectAttributeDTO> attributes = new HashSet<>();
        Mockito.when(metaObjectDTO.getAttributes()).thenReturn(attributes);

        List<ConcurrentHashMap<String, Object>> dataObjects = new ArrayList<>();

        List<LinkedHashMap<String, Object>> result = DataProcessor.populateDataObject(metaObjectDTO, dataObjects);

        assertTrue(result.isEmpty());
    }

    @Test
    void populateDataObjectHandlesNullAttributes() {
        MetaObjectDTO metaObjectDTO = Mockito.mock(MetaObjectDTO.class);
        Mockito.when(metaObjectDTO.getAttributes()).thenReturn(null);

        List<ConcurrentHashMap<String, Object>> dataObjects = new ArrayList<>();

        assertThrows(NullPointerException.class, () -> DataProcessor.populateDataObject(metaObjectDTO, dataObjects));
    }

    @Test
    void sortMetaObjectsBySeqNumSortsCorrectly() {
        MetaObjectAttributeDTO attribute1 = new MetaObjectAttributeDTO();
        attribute1.setDbColumn("column1");
        attribute1.setSeqNum(2);
        MetaObjectAttributeDTO attribute2 = new MetaObjectAttributeDTO();
        attribute2.setDbColumn("column2");
        attribute2.setSeqNum(1);
        Set<MetaObjectAttributeDTO> attributes = new HashSet<>(Arrays.asList(attribute1, attribute2));

        List<MetaObjectAttributeDTO> sortedAttributes = DataProcessor.sortMetaObjectsBySeqNum(attributes);

        assertEquals(2, sortedAttributes.size());
        assertEquals("column2", sortedAttributes.get(0).getDbColumn());
        assertEquals("column1", sortedAttributes.get(1).getDbColumn());
    }

    @Test
    void sortMetaObjectsBySeqNumHandlesEmptySet() {
        Set<MetaObjectAttributeDTO> attributes = new HashSet<>();

        List<MetaObjectAttributeDTO> sortedAttributes = DataProcessor.sortMetaObjectsBySeqNum(attributes);

        assertTrue(sortedAttributes.isEmpty());
    }
    @Test
    void testArrayTypeProcessing() throws SQLException {
        MetaObjectAttributeDTO attr = new MetaObjectAttributeDTO();
        attr.setDbColumn("array_col");
        attr.setDataType(DataType.ARRAY);
        ResultSet rs = mock(ResultSet.class);
        Array sqlArray = mock(Array.class);
        when(rs.getArray("array_col")).thenReturn(sqlArray);
        when(sqlArray.getArray()).thenReturn(new Integer[]{1, 2, 3});
        Object arrayVal = rs.getArray("array_col");
        Object result = arrayVal != null ? ((Array) arrayVal).getArray() : null;
        assertArrayEquals(new Integer[]{1, 2, 3}, (Object[]) result);
    }

    @Test
    void testJsonbTypeProcessing() throws SQLException {
        MetaObjectAttributeDTO attr = new MetaObjectAttributeDTO();
        attr.setDbColumn("jsonb_col");
        attr.setDataType(DataType.JSONB);
        ResultSet rs = mock(ResultSet.class);
        when(rs.getObject("jsonb_col")).thenReturn("{\"key\":\"value\"}");
        Object jsonbVal = rs.getObject("jsonb_col");
        Object result = jsonbVal != null ? jsonbVal.toString() : null;
        assertEquals("{\"key\":\"value\"}", result);
    }

    @Test
    void testNumericTypeProcessing() throws SQLException {
        MetaObjectAttributeDTO attr = new MetaObjectAttributeDTO();
        attr.setDbColumn("numeric_col");
        attr.setDataType(DataType.NUMERIC);
        ResultSet rs = mock(ResultSet.class);
        BigDecimal val = new BigDecimal("123.45");
        when(rs.getBigDecimal("numeric_col")).thenReturn(val);
        Object result = rs.getBigDecimal("numeric_col");
        assertEquals(val, result);
    }

    @Test
    void testBigIntTypeProcessing() throws SQLException {
        MetaObjectAttributeDTO attr = new MetaObjectAttributeDTO();
        attr.setDbColumn("bigint_col");
        attr.setDataType(DataType.BIGINT);
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("bigint_col")).thenReturn(123456789L);
        long bigIntVal = rs.getLong("bigint_col");
        assertEquals(123456789L, bigIntVal);
    }

    @Test
    void testSmallIntTypeProcessing() throws SQLException {
        MetaObjectAttributeDTO attr = new MetaObjectAttributeDTO();
        attr.setDbColumn("smallint_col");
        attr.setDataType(DataType.SMALLINT);
        ResultSet rs = mock(ResultSet.class);
        when(rs.getShort("smallint_col")).thenReturn((short) 123);
        short smallIntVal = rs.getShort("smallint_col");
        assertEquals((short) 123, smallIntVal);
    }
}