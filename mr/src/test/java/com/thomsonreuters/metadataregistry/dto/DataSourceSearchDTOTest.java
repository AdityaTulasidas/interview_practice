package com.thomsonreuters.metadataregistry.dto;

import com.thomsonreuters.metadataregistry.model.dto.DataSourceSearchDTO;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class DataSourceSearchDTOTest {
    @Test
    void shouldSetAndGetFields_WhenValidValuesProvided() {
        DataSourceSearchDTO<String> dto = new DataSourceSearchDTO<>();
        dto.setDisplayName("TestName");
        dto.setDomain("BF_AR");
        dto.setCustomerTenantId("Tenant1");
        dto.setRegionalTenantId("RegionTenant1");
        dto.setOnesourceRegion("APAC");
        dto.setNumberOfRecords(5);
        dto.setItems(List.of("item1", "item2"));

        DataSourceSearchDTO.Meta meta = new DataSourceSearchDTO.Meta();
        meta.setCount(10L);
        meta.setLimit(2);
        meta.setOffset(0);
        dto.setMeta(meta);

        assertEquals("TestName", dto.getDisplayName());
        assertEquals("BF_AR", dto.getDomain());
        assertEquals("Tenant1", dto.getCustomerTenantId());
        assertEquals("RegionTenant1", dto.getRegionalTenantId());
        assertEquals("APAC", dto.getOnesourceRegion());
        assertEquals(5, dto.getNumberOfRecords());
        assertEquals(List.of("item1", "item2"), dto.getItems());
        assertEquals(meta, dto.getMeta());
        assertEquals(10L, dto.getMeta().getCount());
        assertEquals(2, dto.getMeta().getLimit());
        assertEquals(0, dto.getMeta().getOffset());
    }
}
