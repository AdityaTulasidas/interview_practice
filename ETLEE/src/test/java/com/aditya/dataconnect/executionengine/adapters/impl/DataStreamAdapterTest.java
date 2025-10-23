package com.aditya.dataconnect.executionengine.adapters.impl;

import com.thomsonreuters.dataconnect.common.executioncontext.*;
import com.aditya.dataconnect.executionengine.adapters.CommonAdaptersUtil;
import com.aditya.dataconnect.executionengine.data.DataSetCollection;
import com.aditya.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.aditya.dataconnect.executionengine.model.pojo.DataUnit;
import com.aditya.dataconnect.executionengine.model.pojo.Header;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataStreamAdapterTest {
    @InjectMocks
    private DataStreamAdapter dataStreamAdapter;

    @Mock
    private ExecutionContext ctx;
    @Mock
    private GlobalContext globalContext;
    @Mock
    private AdapterContext adapterContext;
    @Mock
    private MessagingContext messagingContext;
    @Mock
    private RegionalJobContext regionalJobContext;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dataStreamAdapter = new DataStreamAdapter();
        dataStreamAdapter.targetsRabbitMQTemplates = new HashMap<>();
        dataStreamAdapter.targetsRabbitMQExchanges = new HashMap<>();
        dataStreamAdapter.targetsRabbitMQRoutingKeys = new HashMap<>();
        dataStreamAdapter.commonAdaptersUtil = mock(CommonAdaptersUtil.class);
        when(dataStreamAdapter.commonAdaptersUtil.generateHeader(any())).thenReturn(new Header());
    }

    @Test
    void shouldNotThrowWhenValidateIsCalled() {
        assertDoesNotThrow(() -> dataStreamAdapter.validate());
    }

    @Test
    void shouldReturnEmptyDataSetCollectionWhenReadDataIsCalled() throws DataSyncJobException {
        when(ctx.getContextByName(eq(ExecutionContext.IN_ADAPTER_CONTEXT))).thenReturn(adapterContext);
        DataUnit dataUnit =new DataUnit();
        dataUnit.setContent(new DataSetCollection());
        when(adapterContext.getValue(DataStreamAdapter.DATA_UNIT)).thenReturn(dataUnit);
        when(ctx.getContextByName(eq(ExecutionContext.REGIONAL_JOB_CONTEXT))).thenReturn(regionalJobContext);
        when(regionalJobContext.getValue(RegionalJobContext.JOB_ID)).thenReturn(UUID.randomUUID());
        assertNotNull(dataStreamAdapter.readData(ctx));
    }

    @Test
    void shouldThrowExceptionWhenWriteDataWithNoTargetRegions() {
        when(ctx.getContextByName(eq(ExecutionContext.GLOBAL_CONTEXT))).thenReturn(globalContext);
        when(globalContext.getValue(eq(GlobalContext.MESSAGING_CONTEXT))).thenReturn(messagingContext);
        when(ctx.getContextByName(eq(ExecutionContext.REGIONAL_JOB_CONTEXT))).thenReturn(regionalJobContext);
        when(regionalJobContext.getValue(anyString())).thenReturn("");
        assertThrows(IllegalStateException.class, () -> dataStreamAdapter.writeData(new DataSetCollection(), ctx));
    }

    @Test
    void shouldSendMessagesToAllTargetsWhenWriteDataWithValidRegions() {
        when(ctx.getContextByName(eq(ExecutionContext.GLOBAL_CONTEXT))).thenReturn(globalContext);
        when(globalContext.getValue(eq(GlobalContext.MESSAGING_CONTEXT))).thenReturn(messagingContext);
        when(ctx.getContextByName(eq(ExecutionContext.REGIONAL_JOB_CONTEXT))).thenReturn(regionalJobContext);
        when(regionalJobContext.getValue(eq(RegionalJobContext.TARGET_REGIONS))).thenReturn("AMER,EMEA");
        when(regionalJobContext.getValue(eq(RegionalJobContext.JOB_ID))).thenReturn(UUID.randomUUID());
        when(regionalJobContext.getValue(eq(RegionalJobContext.EXEC_ID))).thenReturn(UUID.randomUUID());
        dataStreamAdapter.targetsRabbitMQTemplates.put("AMER", rabbitTemplate);
        dataStreamAdapter.targetsRabbitMQTemplates.put("EMEA", rabbitTemplate);
        dataStreamAdapter.targetsRabbitMQExchanges.put("AMER", "exchange1");
        dataStreamAdapter.targetsRabbitMQExchanges.put("EMEA", "exchange2");
        dataStreamAdapter.targetsRabbitMQRoutingKeys.put("AMER", "key1");
        dataStreamAdapter.targetsRabbitMQRoutingKeys.put("EMEA", "key2");
        assertDoesNotThrow(() -> dataStreamAdapter.writeData(new DataSetCollection(), ctx));
        verify(rabbitTemplate, times(2)).convertAndSend(anyString(), anyString(), (Object)any());
    }

    @Test
    void shouldNotThrowWhenCleanUpIsCalled() {
        assertDoesNotThrow(() -> dataStreamAdapter.cleanUp());
    }
}
