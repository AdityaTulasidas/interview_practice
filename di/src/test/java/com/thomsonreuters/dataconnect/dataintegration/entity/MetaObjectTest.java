package com.thomsonreuters.dataconnect.dataintegration.entity;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.MetaObject;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MetaObjectTest {

    @Test
    void shouldSetFields_WhenMetaObjectIsCreated() {
        String description = "Test description";
        String tableName = "test_table";
        String domain = "BF_AR";
        String displayName = "Test Display Name";
        boolean isAutogenId = true;
        String schema = "test_schema";

        MetaObject metaObject = new MetaObject(UUID.randomUUID(),description, tableName, schema,displayName,domain, "system_name","domain_object" ,1,isAutogenId,false, new HashSet<>(), new HashSet<>());

        assertEquals(description, metaObject.getDescription());
        assertEquals(tableName, metaObject.getDbTable());
        assertEquals(domain, metaObject.getOneSourceDomain());
        assertEquals(displayName, metaObject.getDisplayName());
        assertTrue(metaObject.isAutogenId());
    }

    @Test
    void shouldInitializeChildRelationsWhenMetaObjectIsCreated() {
        MetaObject metaObject = new MetaObject();
        assertNotNull(metaObject.getChildRelations());
        assertTrue(metaObject.getChildRelations() instanceof HashSet);
    }
}