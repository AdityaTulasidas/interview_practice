package com.thomsonreuters.dataconnect.dataintegration.entity.pojo;

import com.thomsonreuters.dataconnect.dataintegration.model.pojo.DataUnit;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.DatasyncMessage;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.Header;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatasyncMessageTest {

    @Test
    void shouldSetAndGetHeaderAndDataCorrectly_WhenValuesAreProvided() {
        Header header = new Header();
        DataUnit data = new DataUnit();

        DatasyncMessage message = new DatasyncMessage();
        message.setHeader(header);
        message.setData(data);

        assertEquals(header, message.getHeader());
        assertEquals(data, message.getData());
    }

    @Test
    void shouldInitializeFieldsToNull_WhenNoArgsConstructorIsUsed() {
        DatasyncMessage message = new DatasyncMessage();
        assertNull(message.getHeader());
        assertNull(message.getData());
    }

    @Test
    void shouldSetHeaderAndDataCorrectly_WhenAllArgsConstructorIsUsed() {
        Header header = new Header();
        DataUnit data = new DataUnit();

        DatasyncMessage message = new DatasyncMessage();
        message.setHeader(header);
        message.setData(data);

        assertEquals(header, message.getHeader());
        assertEquals(data, message.getData());
    }

    @Test
    void shouldGenerateNonNullStringRepresentation_WhenToStringIsCalled() {
        Header header = new Header();
        DataUnit data = new DataUnit();

        DatasyncMessage message = new DatasyncMessage();
        message.setHeader(header);
        message.setData(data);

        assertNotNull(message.toString());
    }

    @Test
    void shouldBeEqualAndHaveSameHashCode_WhenObjectsHaveSameValues() {
        Header header = new Header();
        DataUnit data = new DataUnit();

        DatasyncMessage message1 = new DatasyncMessage();
        message1.setHeader(header);
        message1.setData(data);

        DatasyncMessage message2 = new DatasyncMessage();
        message2.setHeader(header);
        message2.setData(data);

        assertEquals(message1, message2);
        assertEquals(message1.hashCode(), message2.hashCode());
    }
}