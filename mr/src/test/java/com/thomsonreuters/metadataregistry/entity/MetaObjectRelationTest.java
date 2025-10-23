package com.thomsonreuters.metadataregistry.entity;

import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import com.thomsonreuters.metadataregistry.model.entity.MetaObjectRelation;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MetaObjectRelationTest {

    @Test
    void shouldSetFields_WhenMetaObjectRelationIsCreated() {
        String description = "Test relation";
        String systemName = "rel-1";
        MetaObject parentObject = new MetaObject();
        String parentRelCol = "parent_col";
        String childRelCol = "child_col";
        MetaObject childObject = new MetaObject();
        String relationType = "PARENT";

        MetaObjectRelation relation = new MetaObjectRelation(UUID.randomUUID(),description, systemName, parentObject, parentRelCol, childRelCol, childObject, relationType);

        assertEquals(description, relation.getDescription());
        assertEquals(systemName, relation.getSystemName());
        assertEquals(parentObject, relation.getParentObject());
        assertEquals(parentRelCol, relation.getParentObjRelCol());
        assertEquals(childRelCol, relation.getChildObjRelCol());
        assertEquals(childObject, relation.getChildObject());
        assertEquals(relationType, relation.getRelationType());
    }
}