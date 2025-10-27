package com.thomsonreuters.dataconnect.dataintegration.entity.pojo;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.MessageType;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.OperationType;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.Header;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HeaderTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly_WhenValuesAreProvided() {
        // Create test data
        UUID id = UUID.randomUUID();
        String jobName = "Test Job";
        UUID execId = UUID.randomUUID();
        MessageType msgType = MessageType.TRANSFER;
        String metaObject = "Test Meta Object";
        String source = "AMER";
        String target = "AMER";
        OperationType opType = OperationType.CREATE;
        Boolean isTarget = true;
        Boolean isSource = false;

        // Create and set values in Header
        Header header = new Header();
        header.setJobId(id);
        header.setJobName(jobName);
        header.setJobExecId(execId);
        header.setSource(source);
        header.setTarget(target);

        // Assert values are correctly set
        assertEquals(id, header.getJobId());
        assertEquals(jobName, header.getJobName());
        assertEquals(execId, header.getJobExecId());
        assertEquals(source, header.getSource());
        assertEquals(target, header.getTarget());
    }

    @Test
    void shouldInitializeAllFieldsToNull_WhenNoArgsConstructorIsUsed() {
        // Test the no-args constructor
        Header header = new Header();
        assertNull(header.getJobId());
        assertNull(header.getJobName());
        assertNull(header.getJobExecId());
        assertNull(header.getSource());
        assertNull(header.getTarget());
    }

    @Test
    void shouldGenerateNonNullStringRepresentation_WhenToStringIsCalled() {
        // Create a Header instance
        Header header = new Header();
        header.setJobName("Test Job");

        // Assert the toString method does not throw exceptions
        assertNotNull(header.toString());
    }

    @Test
    void shouldBeEqualAndHaveSameHashCode_WhenFieldsAreIdentical() {
        // Create two identical Header objects
        UUID id = UUID.randomUUID();
        Header header1 = new Header();
        header1.setJobId(id);

        Header header2 = new Header();
        header2.setJobId(id);

        // Assert equality and hashCode
        assertEquals(header1, header2);
        assertEquals(header1.hashCode(), header2.hashCode());
    }
}