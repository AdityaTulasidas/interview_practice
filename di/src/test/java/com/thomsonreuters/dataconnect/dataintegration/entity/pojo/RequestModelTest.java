package com.thomsonreuters.dataconnect.dataintegration.entity.pojo;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.OperationType;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.RequestBodyDataUnitList;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.RequestModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestModelTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly_WhenValuesAreProvided() {
        // Create test data
        String domain = "BF_AR";
        String metaObjectName = "Test Meta Object";
        String customerTenantSysName = "Tenant123";
        OperationType operationType = OperationType.CREATE;
        RequestBodyDataUnitList requestDataUnitList = new RequestBodyDataUnitList();

        // Create and set values in RequestModel
        RequestModel requestModel = new RequestModel();
        requestModel.setMetaObjectName(metaObjectName);
        requestModel.setCustomerTenantSysName(customerTenantSysName);
        requestModel.setOperationType(operationType);
        requestModel.setRequestDataUnitList(requestDataUnitList);

        // Assert values are correctly set
        assertEquals(metaObjectName, requestModel.getMetaObjectName());
        assertEquals(customerTenantSysName, requestModel.getCustomerTenantSysName());
        assertEquals(operationType, requestModel.getOperationType());
        assertEquals(requestDataUnitList, requestModel.getRequestDataUnitList());
    }

    @Test
    void shouldInitializeAllFieldsToNull_WhenNoArgsConstructorIsUsed() {
        // Test the no-args constructor
        RequestModel requestModel = new RequestModel();
        assertNull(requestModel.getMetaObjectName());
        assertNull(requestModel.getCustomerTenantSysName());
        assertNull(requestModel.getOperationType());
        assertNull(requestModel.getRequestDataUnitList());
    }

    @Test
    void shouldGenerateNonNullStringRepresentation_WhenToStringIsCalled() {
        // Create a RequestModel instance
        RequestModel requestModel = new RequestModel();
        requestModel.setMetaObjectName("Test Meta Object");

        // Assert the toString method does not throw exceptions
        assertNotNull(requestModel.toString());
    }

    @Test
    void shouldBeEqualAndHaveSameHashCode_WhenFieldsAreIdentical() {
        // Create two identical RequestModel objects
        String domain = "BF_AR";
        String metaObjectName = "Test Meta Object";
        String customerTenantSysName = "Tenant123";
        OperationType operationType = OperationType.CREATE;
        RequestBodyDataUnitList requestDataUnitList = new RequestBodyDataUnitList();

        RequestModel requestModel1 = new RequestModel();
        requestModel1.setMetaObjectName(metaObjectName);
        requestModel1.setCustomerTenantSysName(customerTenantSysName);
        requestModel1.setOperationType(operationType);
        requestModel1.setRequestDataUnitList(requestDataUnitList);

        RequestModel requestModel2 = new RequestModel();
        requestModel2.setMetaObjectName(metaObjectName);
        requestModel2.setCustomerTenantSysName(customerTenantSysName);
        requestModel2.setOperationType(operationType);
        requestModel2.setRequestDataUnitList(requestDataUnitList);

        // Assert equality and hashCode
        assertEquals(requestModel1, requestModel2);
        assertEquals(requestModel1.hashCode(), requestModel2.hashCode());
    }
}