package com.thomsonreuters.dataconnect.executionengine.entity;

import com.thomsonreuters.dataconnect.executionengine.model.entity.DataSource;


import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DataSourceTest {

    @Test
    void testGetterAndSetterMethods() {
        DataSource dataSource = new DataSource();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        dataSource.setId(id);
        dataSource.setDisplayName("Test Display Name");
        dataSource.setSystemName("Test System Name");
        dataSource.setDescription("Test Description");
        dataSource.setDbType("POSTGRESQL");
        dataSource.setDomain("BF_AR");
        dataSource.setRegionalTenantId("regionalTenantId");
        dataSource.setCustomerTenantId("customerTenantId");
        dataSource.setOnesourceRegion("AMER");
        dataSource.setUserName("test-user");
        dataSource.setPassword("test-pass");
        dataSource.setDomainObjectSysName("TestDomainObject");
        dataSource.setHost("localhost");
        dataSource.setDb("testdb");
        dataSource.setPort("1521");
        dataSource.setCreatedAt(now);
        dataSource.setUpdatedAt(now);

        assertEquals(id, dataSource.getId());
        assertEquals("Test Display Name", dataSource.getDisplayName());
        assertEquals("Test System Name", dataSource.getSystemName());
        assertEquals("Test Description", dataSource.getDescription());
        assertEquals("POSTGRESQL", dataSource.getDbType());
        assertEquals("BF_AR", dataSource.getDomain());
        assertEquals("regionalTenantId", dataSource.getRegionalTenantId());
        assertEquals("customerTenantId", dataSource.getCustomerTenantId());
        assertEquals("AMER", dataSource.getOnesourceRegion());
        assertEquals("test-user", dataSource.getUserName());
        assertEquals("test-pass", dataSource.getPassword());
        assertEquals("TestDomainObject", dataSource.getDomainObjectSysName());
        assertEquals("localhost", dataSource.getHost());
        assertEquals("testdb", dataSource.getDb());
        assertEquals("1521", dataSource.getPort());
        assertEquals(now, dataSource.getCreatedAt());
        assertEquals(now, dataSource.getUpdatedAt());
    }



//    @Test
//    void testValidationAnnotations() {
//        DataSource dataSource = new DataSource();
//
//        //assertThrows(NullPointerException.class, () -> dataSource.setName(null));
//        assertThrows(IllegalArgumentException.class, () -> dataSource.setName(""));
//        assertThrows(IllegalArgumentException.class, () -> dataSource.setName("null"));
//
//        assertThrows(NullPointerException.class, () -> dataSource.setDbType(null));
//        assertThrows(NullPointerException.class, () -> dataSource.setOnesourceDomain(null));
//        assertThrows(NullPointerException.class, () -> dataSource.setOnesourceRegion(null));
//        assertThrows(NullPointerException.class, () -> dataSource.setProductName(null));
//        assertThrows(NullPointerException.class, () -> dataSource.setJdbcConnectUrl(null));
//    }
}