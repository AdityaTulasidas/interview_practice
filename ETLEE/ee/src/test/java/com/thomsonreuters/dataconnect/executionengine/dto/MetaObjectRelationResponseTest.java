package com.thomsonreuters.dataconnect.executionengine.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MetaObjectRelationResponseTest {

    @Test
    void testGetterAndSetter() {
        MetaObjectRelationResponse response = new MetaObjectRelationResponse();

        MetaObjectRelationDTO relation1 = new MetaObjectRelationDTO();
        MetaObjectRelationDTO relation2 = new MetaObjectRelationDTO();
        List<MetaObjectRelationDTO> relations = List.of(relation1, relation2);

        response.setMetaObjectRelations(relations);

        assertNotNull(response.getMetaObjectRelations());
        assertEquals(2, response.getMetaObjectRelations().size());
        assertEquals(relation1, response.getMetaObjectRelations().get(0));
        assertEquals(relation2, response.getMetaObjectRelations().get(1));
    }

    @Test
    void testDefaultValues() {
        MetaObjectRelationResponse response = new MetaObjectRelationResponse();
        assertNotNull(response);
        assertEquals(null, response.getMetaObjectRelations());
    }
}