package com.thomsonreuters.dataconnect.dataintegration.entity;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.MetaObject;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.MetaObjectRelation;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.RelationType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MetaObjectRelationTest {

    @Test
    void shouldInitializeAllFieldsToNull_WhenNoArgsConstructorIsUsed() {
        // Test the no-args constructor
        MetaObjectRelation relation = new MetaObjectRelation();
        assertNotNull(relation);
    }

    @Test
    void shouldSetAllFieldsCorrectly_WhenParameterizedConstructorIsUsed() {
        // Test the parameterized constructor
        MetaObject parentObject = new MetaObject();
        MetaObject childObject = new MetaObject();
        MetaObjectRelation relation = new MetaObjectRelation(
                UUID.randomUUID(),
                "Test Description",
                "relationId",
                parentObject,
                "parentCol",
                "childCol",
                childObject,
                "PARENT"
        );

        assertEquals("Test Description", relation.getDescription());
        assertEquals("relationId", relation.getSystemName());
        assertEquals(parentObject, relation.getParentObject());
        assertEquals("parentCol", relation.getParentObjRelCol());
        assertEquals("childCol", relation.getChildObjRelCol());
        assertEquals(childObject, relation.getChildObject());
        assertEquals("PARENT", relation.getRelationType());
    }

    @Test
    void shouldSetAndGetAllFieldsCorrectly_WhenGettersAndSettersAreUsed() {
        // Create a MetaObjectRelation instance
        MetaObjectRelation relation = new MetaObjectRelation();
        MetaObject parentObject = new MetaObject();
        MetaObject childObject = new MetaObject();

        // Set values
        UUID id = UUID.randomUUID();
        relation.setId(id);
        relation.setDescription("Test Description");
        relation.setSystemName("relationId");
        relation.setParentObject(parentObject);
        relation.setParentObjRelCol("parentCol");
        relation.setChildObjRelCol("childCol");
        relation.setChildObject(childObject);
        relation.setRelationType("PARENT");

        // Assert values
        assertEquals(id, relation.getId());
        assertEquals("Test Description", relation.getDescription());
        assertEquals("relationId", relation.getSystemName());
        assertEquals(parentObject, relation.getParentObject());
        assertEquals("parentCol", relation.getParentObjRelCol());
        assertEquals("childCol", relation.getChildObjRelCol());
        assertEquals(childObject, relation.getChildObject());
        assertEquals("PARENT", relation.getRelationType());
    }


    @Test
    void shouldGenerateNonNullStringRepresentation_WhenToStringIsCalled() {
        // Test the toString method
        MetaObjectRelation relation = new MetaObjectRelation();
        relation.setDescription("Test Description");
        assertNotNull(relation.toString());
    }
}