package com.thomsonreuters.metadataregistry.dto;

import com.thomsonreuters.metadataregistry.model.dto.MetaObjectDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MetaObjectDTOTest {

  @Test
  void shouldSetAndGetFields_WhenValidValuesProvided() {
      MetaObjectDTO metaObjectDTO = new MetaObjectDTO();
      metaObjectDTO.setDescription("Test Description");
      metaObjectDTO.setDbTable("Test.Table");
      metaObjectDTO.setDisplayName("Test Display Name");
      metaObjectDTO.setAutogenId(true);

      assertEquals("Test Description", metaObjectDTO.getDescription());
      assertEquals("Test.Table", metaObjectDTO.getDbTable());
      assertEquals("Test Display Name", metaObjectDTO.getDisplayName());
      assertTrue(metaObjectDTO.isAutogenId());
      assertNotNull(metaObjectDTO.getAttributes()); // New assertion
      assertTrue(metaObjectDTO.getAttributes().isEmpty()); // New assertion
  }

}