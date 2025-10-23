package com.thomsonreuters.dataconnect.executionengine.entity;//package com.thomsonreuters.dataconnect.executionengine.entity;
//
//import com.thomsonreuters.dataconnect.executionengine.model.entity.MetaObject;
//import com.thomsonreuters.dataconnect.executionengine.model.entity.MetaObjectAttribute;
//import com.thomsonreuters.dataconnect.executionengine.model.entity.MetaObjectRelation;
//import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.OnesourceDomain;
//import org.junit.jupiter.api.Test;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class MetaObjectTest {
//
//    @Test
//    void testConstructorAndGetters() {
//        String description = "Test Description";
//        String tableName = "test_table";
//        OnesourceDomain oneSourceDomain = OnesourceDomain.BF_AR;
//        String displayName = "Test Display Name";
//        boolean isAutogenId = true;
//
//        MetaObject metaObject = new MetaObject(description, tableName, oneSourceDomain, displayName, isAutogenId);
//
//        assertEquals(description, metaObject.getDescription());
//        assertEquals(tableName, metaObject.getTableName());
//        assertEquals(oneSourceDomain, metaObject.getOneSourceDomain());
//        assertEquals(displayName, metaObject.getDisplayName());
//        assertTrue(metaObject.isAutogenId());
//    }
//
//    @Test
//    void testNoArgsConstructorAndSetters() {
//        MetaObject metaObject = new MetaObject();
//
//        UUID id = UUID.randomUUID();
//        String description = "Test Description";
//        String tableName = "test_table";
//        OnesourceDomain oneSourceDomain = OnesourceDomain.BF_AR;
//        String name = "Test Name";
//        String displayName = "Test Display Name";
//        boolean isAutogenId = true;
//        Map<String, MetaObjectAttribute> attributes = new HashMap<>();
//        Set<MetaObjectRelation> childRelations = new HashSet<>();
//
//        metaObject.setId(id);
//        metaObject.setDescription(description);
//        metaObject.setTableName(tableName);
//        metaObject.setOneSourceDomain(oneSourceDomain);
//        metaObject.setName(name);
//        metaObject.setDisplayName(displayName);
//        metaObject.setAutogenId(isAutogenId);
//        metaObject.setAttributes((Set<MetaObjectAttribute>) attributes);
//        metaObject.setChildRelations(childRelations);
//
//        assertEquals(id, metaObject.getId());
//        assertEquals(description, metaObject.getDescription());
//        assertEquals(tableName, metaObject.getTableName());
//        assertEquals(oneSourceDomain, metaObject.getOneSourceDomain());
//        assertEquals(name, metaObject.getName());
//        assertEquals(displayName, metaObject.getDisplayName());
//        assertTrue(metaObject.isAutogenId());
//        assertEquals(attributes, metaObject.getAttributes());
//        assertEquals(childRelations, metaObject.getChildRelations());
//    }
//
//    @Test
//    void testDefaultValues() {
//        MetaObject metaObject = new MetaObject();
//        assertNotNull(metaObject.getAttributes());
//        assertNotNull(metaObject.getChildRelations());
//    }
//
//    @Test
//    void testRelationships() {
//        MetaObject metaObject = new MetaObject();
//
//        MetaObjectAttribute attribute = new MetaObjectAttribute();
//        metaObject.getAttributes().put("key", attribute);
//
//        MetaObjectRelation relation = new MetaObjectRelation();
//        metaObject.getChildRelations().add(relation);
//
//        assertEquals(1, metaObject.getAttributes().size());
//        assertEquals(attribute, metaObject.getAttributes().get("key"));
//        assertEquals(1, metaObject.getChildRelations().size());
//        assertTrue(metaObject.getChildRelations().contains(relation));
//    }
//}