package com.aditya.dataconnect.executionengine.pojo;//package com.thomsonreuters.dataconnect.executionengine.pojo;
//
//import com.thomsonreuters.dataconnect.executionengine.model.pojo.Header;
//import org.junit.jupiter.api.Test;
//
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class HeaderTest {
//
//    @Test
//    void testGetterAndSetterMethods() {
//        UUID id = UUID.randomUUID();
//        String jobName = "TestJob";
//        UUID execId = UUID.randomUUID();
//        String msgType = "INFO";
//        String metaObject = "MetaData";
//        String source = "SourceSystem";
//        String target = "TargetSystem";
//        String opType = "INSERT";
//        Boolean isTarget = true;
//        Boolean isSource = false;
//
//        Header header = new Header();
//        header.setId(id);
//        header.setJobName(jobName);
//        header.setExecId(execId);
//        header.setMsgType(msgType);
//        header.setMetaObject(metaObject);
//        header.setSource(source);
//        header.setTarget(target);
//        header.setOpType(opType);
//        header.setIsTarget(isTarget);
//        header.setIsSource(isSource);
//
//        assertEquals(id, header.getId());
//        assertEquals(jobName, header.getJobName());
//        assertEquals(execId, header.getExecId());
//        assertEquals(msgType, header.getMsgType());
//        assertEquals(metaObject, header.getMetaObject());
//        assertEquals(source, header.getSource());
//        assertEquals(target, header.getTarget());
//        assertEquals(opType, header.getOpType());
//        assertEquals(isTarget, header.getIsTarget());
//        assertEquals(isSource, header.getIsSource());
//    }
//
//    @Test
//    void testToStringMethod() {
//        Header header = new Header();
//        header.setJobName("TestJob");
//        header.setMsgType("INFO");
//
//        String toString = header.toString();
//        assertNotNull(toString);
//        assertTrue(toString.contains("jobName"));
//        assertTrue(toString.contains("msgType"));
//    }
//
//    @Test
//    void testEqualsAndHashCode() {
//        Header header1 = new Header();
//        Header header2 = new Header();
//
//        assertEquals(header1, header2);
//        assertEquals(header1.hashCode(), header2.hashCode());
//
//        header1.setId(UUID.randomUUID());
//        assertNotEquals(header1, header2);
//        assertNotEquals(header1.hashCode(), header2.hashCode());
//    }
//}