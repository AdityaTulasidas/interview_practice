package com.thomsonreuters.dataconnect.executionengine.pojo;

import com.thomsonreuters.dataconnect.executionengine.model.pojo.DataUnit;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.DatasyncMessage;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.Header;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatasyncMessageTest {

    @Test
    void testGetterAndSetterMethods() {
        Header header = new Header();
        DataUnit data = new DataUnit();

        DatasyncMessage message = new DatasyncMessage();
        message.setHeader(header);
        message.setData(data);

        assertEquals(header, message.getHeader());
        assertEquals(data, message.getData());
    }

    @Test
    void testToStringMethod() {
        DatasyncMessage message = new DatasyncMessage();
        message.setHeader(new Header());
        message.setData(new DataUnit());

        String toString = message.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("header"));
        assertTrue(toString.contains("data"));
    }

    @Test
    void testEqualsAndHashCode() {
        DatasyncMessage message1 = new DatasyncMessage();
        DatasyncMessage message2 = new DatasyncMessage();

        assertEquals(message1, message2);
        assertEquals(message1.hashCode(), message2.hashCode());

        message1.setHeader(new Header());
        assertNotEquals(message1, message2);
        assertNotEquals(message1.hashCode(), message2.hashCode());
    }
}