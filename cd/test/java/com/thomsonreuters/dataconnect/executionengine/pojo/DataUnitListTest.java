package com.thomsonreuters.dataconnect.executionengine.pojo;//package com.thomsonreuters.dataconnect.executionengine.pojo;
//
//import com.thomsonreuters.dataconnect.executionengine.model.pojo.DataUnitList;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class DataUnitListTest {
//
//    @Test
//    void testGetterAndSetterMethods() {
//        List<UUID> objId = List.of(UUID.randomUUID(), UUID.randomUUID());
//        DataUnitList dataUnitList = new DataUnitList();
//        dataUnitList.setObjIds(objId);
//
//        assertEquals(objId, dataUnitList.getObjIds());
//    }
//
//    @Test
//    void testToStringMethod() {
//        DataUnitList dataUnitList = new DataUnitList();
//        dataUnitList.setObjIds(List.of(UUID.randomUUID()));
//
//        String toString = dataUnitList.toString();
//        assertNotNull(toString);
//        assertTrue(toString.contains("objId"));
//    }
//
//    @Test
//    void testEqualsAndHashCode() {
//        DataUnitList dataUnitList1 = new DataUnitList();
//        DataUnitList dataUnitList2 = new DataUnitList();
//
//        assertEquals(dataUnitList1, dataUnitList2);
//        assertEquals(dataUnitList1.hashCode(), dataUnitList2.hashCode());
//
//        dataUnitList1.setObjIds(List.of(UUID.randomUUID()));
//        assertNotEquals(dataUnitList1, dataUnitList2);
//        assertNotEquals(dataUnitList1.hashCode(), dataUnitList2.hashCode());
//    }
//}