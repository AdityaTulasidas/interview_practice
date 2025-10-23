package com.thomsonreuters.dataconnect.executionengine.adapters.impl;

import com.thomsonreuters.dataconnect.common.executioncontext.AdapterContext;
import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.thomsonreuters.dataconnect.common.executioncontext.GlobalContext;
import com.thomsonreuters.dataconnect.common.executioncontext.RegionalJobContext;
import com.thomsonreuters.dataconnect.executionengine.adapters.CommonAdaptersUtil;
import com.thomsonreuters.dataconnect.executionengine.constant.Constants;
import com.thomsonreuters.dataconnect.executionengine.data.DataSet;
import com.thomsonreuters.dataconnect.executionengine.data.DataSetCollection;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectAttributeDTO;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectDTO;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaRelationMetaModelDTO;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.entity.MetaObject;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.DataType;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.DataUnit;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.DataUnitList;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.PrimaryKeyInfo;
import com.thomsonreuters.dataconnect.executionengine.repository.MetaObjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseAdapterTest {
    private DatabaseAdapter databaseAdapter;
    @Mock
    private ExecutionContext ctx;
    @Mock
    private AdapterContext adapterContext;
    @Mock
    private RegionalJobContext regionalJobContext;
    @Mock
    private CommonAdaptersUtil commonAdaptersUtil;
    @Mock
    private Connection connection;
    @Mock
    private MetaObjectRepository metaObjectRepository;

    @Mock
    private GlobalContext globalContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        databaseAdapter = new DatabaseAdapter();
        databaseAdapter.commonAdaptersUtil = commonAdaptersUtil;
    }

    @Test
    void whenReadData_withMetaRelationNull_thenThrowsException() throws Exception {
        // Arrange minimal required stubs for failure path
        DataUnitList dataUnitList = mock(DataUnitList.class);
        DataUnit dataUnit = mock(DataUnit.class);
        when(dataUnit.getContent()).thenReturn(dataUnitList); // needed to avoid null path
        when(ctx.getContextByName(ExecutionContext.IN_ADAPTER_CONTEXT)).thenReturn(adapterContext);
        when(adapterContext.getValue(DatabaseAdapter.DATA_UNIT)).thenReturn(dataUnit);
        when(ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT)).thenReturn(regionalJobContext);
        // readData pulls META_OBJECT_SYS_NAME then repository.findMetaObjectBySystemName(...).getId()
        when(regionalJobContext.getValue(RegionalJobContext.META_OBJECT_SYS_NAME)).thenReturn("test-meta-sys-name");
        MetaObject metaObject = mock(MetaObject.class);
        when(metaObject.getId()).thenReturn(UUID.randomUUID());
        when(metaObjectRepository.findMetaObjectBySystemName(any())).thenReturn(metaObject);
        // Force fetchMetaRelation to return null to hit exception branch
        when(commonAdaptersUtil.fetchMetaRelation(any(UUID.class))).thenReturn(null);
        DatabaseAdapter spyAdapter = spy(databaseAdapter);
        spyAdapter.metaObjectRepository = metaObjectRepository; // inject mocked repo
        doReturn(connection).when(spyAdapter).getConnection(any(ExecutionContext.class));
        // Act & Assert
        DataSyncJobException ex = assertThrows(DataSyncJobException.class, () -> spyAdapter.readData(ctx));
        assertEquals(Constants.INTERNAL_SERVER_ERROR, ex.getCode());
    }
}
