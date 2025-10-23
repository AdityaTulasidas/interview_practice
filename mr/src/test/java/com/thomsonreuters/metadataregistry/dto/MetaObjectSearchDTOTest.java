package com.thomsonreuters.metadataregistry.dto;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectPostDTO;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class MetaObjectSearchDTOTest {

        @Test
        void shouldSetAndGetFields_WhenValidValuesProvided() {
            MetaObjectPostDTO metaObjectPostDTO = new MetaObjectPostDTO();
            metaObjectPostDTO.setDescription("Test Description");
            metaObjectPostDTO.setDbTable("Test Table");
            metaObjectPostDTO.setDisplayName("Test Display Name");
            metaObjectPostDTO.setAutogenId(true);
            metaObjectPostDTO.setOneSourceDomain("BF_AR");
            metaObjectPostDTO.setSchema("Test Schema");
            metaObjectPostDTO.setDomainObject("Test Domain Object");
            metaObjectPostDTO.setSystemName("Test Meta Object Sys Name");
            metaObjectPostDTO.setEventEnabled(true);
            metaObjectPostDTO.setBusinessName("Test Business Name");
            metaObjectPostDTO.setUsageCount(5);


            assertEquals("Test Description", metaObjectPostDTO.getDescription());
            assertEquals("Test Table", metaObjectPostDTO.getDbTable());
            assertEquals("Test Display Name", metaObjectPostDTO.getDisplayName());
            assertTrue(metaObjectPostDTO.isAutogenId());
            assertEquals("BF_AR", metaObjectPostDTO.getOneSourceDomain());
            assertEquals("Test Schema", metaObjectPostDTO.getSchema());
            assertEquals("Test Domain Object", metaObjectPostDTO.getDomainObject());
            assertEquals("Test Meta Object Sys Name", metaObjectPostDTO.getSystemName());
            assertTrue(metaObjectPostDTO.isEventEnabled());
            assertEquals("Test Business Name", metaObjectPostDTO.getBusinessName());
            assertEquals(5, metaObjectPostDTO.getUsageCount());

            assertTrue(metaObjectPostDTO.isAutogenId());
        }



    }