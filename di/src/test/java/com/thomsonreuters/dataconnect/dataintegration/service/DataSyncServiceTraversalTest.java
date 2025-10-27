package com.thomsonreuters.dataconnect.dataintegration.service;

import com.thomsonreuters.dataconnect.dataintegration.configuration.DataIntegrationRegionConfig;
import com.thomsonreuters.dataconnect.dataintegration.configuration.ModelMapperConfig;
import com.thomsonreuters.dataconnect.dataintegration.configuration.RabbitMQConfig;
import com.thomsonreuters.dataconnect.dataintegration.configuration.RegionConfig;
import com.thomsonreuters.dataconnect.dataintegration.constant.Constants;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.DatasyncJobConfiguration;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.JobExecutionLog;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.MetaObject;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.MetaObjectRelation;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.ExecType;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.OperationType;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.RequestBodyDataUnitList;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.RequestModel;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.DatasyncMessage;
import com.thomsonreuters.dataconnect.dataintegration.repository.DatasyncJobConfigRepository;
import com.thomsonreuters.dataconnect.dataintegration.repository.JobExecutionLogRepository;
import com.thomsonreuters.dataconnect.dataintegration.repository.MetaObjectRelationRepository;
import com.thomsonreuters.dataconnect.dataintegration.repository.MetaObjectRepository;
import com.thomsonreuters.dataconnect.dataintegration.services.DataSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.thomsonreuters.dataconnect.dataintegration.testutil.ReflectionTestHelper.setField;
import static com.thomsonreuters.dataconnect.dataintegration.testutil.ReflectionTestHelper.getField;

/**
 * Tests multi-level fallback lookup logic for publishDataSyncJob(RequestModel).
 * Scenarios:
 *  1. Direct job found for provided meta_object_id (no traversal).
 *  2. No direct job; traverse up 2+ levels to root; job found at root.
 *  3. No direct job; traverse chain; no job at root - expect error.
 *
 * NOTE: Relies on Lombok-generated accessors for entities / model classes.
 */
class DataSyncServiceTraversalTest {

    private DatasyncJobConfigRepository datasyncJobConfigRepository;
    private JobExecutionLogRepository jobExecutionLogRepository;
    private MetaObjectRepository metaObjectRepository;
    private MetaObjectRelationRepository metaObjectRelationRepository;
    private RabbitTemplate rabbitTemplate;
    private DataSyncService service;



    @BeforeEach
    void setup() {
        datasyncJobConfigRepository = mock(DatasyncJobConfigRepository.class);
        jobExecutionLogRepository = mock(JobExecutionLogRepository.class);
        metaObjectRepository = mock(MetaObjectRepository.class);
        metaObjectRelationRepository = mock(MetaObjectRelationRepository.class);
        rabbitTemplate = mock(RabbitTemplate.class);
        var regionConfig = mock(com.thomsonreuters.dataconnect.dataintegration.configuration.RegionConfig.class);
        var dataIntegrationRegionConfig = mock(com.thomsonreuters.dataconnect.dataintegration.configuration.DataIntegrationRegionConfig.class);
        var modelMapperConfig = mock(com.thomsonreuters.dataconnect.dataintegration.configuration.ModelMapperConfig.class);
        var rabbitMQConfig = new com.thomsonreuters.dataconnect.dataintegration.configuration.RabbitMQConfig();
        setField(rabbitMQConfig, "sourceListenerExchange", "ex");
        setField(rabbitMQConfig, "sourceSenderRoutingKey", "rk");
        service = new DataSyncService(
                datasyncJobConfigRepository,
                jobExecutionLogRepository,
                metaObjectRepository,
                rabbitTemplate,
                rabbitMQConfig,
                regionConfig,
                new com.fasterxml.jackson.databind.ObjectMapper(),
                new org.springframework.web.client.RestTemplate(),
                dataIntegrationRegionConfig,
                modelMapperConfig,
                metaObjectRelationRepository
        );
    }

    private RequestModel buildRequest(UUID metaId) {
        RequestModel rm = new RequestModel();
        setField(rm, "metaObjectId", metaId.toString());
        setField(rm, "execType", ExecType.REAL_TIME);
        setField(rm, "operationType", OperationType.CREATE);
        setField(rm, "metaObjectName", "child_name"); // legacy fields present but not used when meta_object_id supplied
        setField(rm, "customerTenantSysName", "customerTenantSysName");
        setField(rm, "sourceTenantId", "sourceTenantId");

        // Add valid object id for real time sync validation
        RequestBodyDataUnitList unitList = new RequestBodyDataUnitList();
        unitList.setObjIds(java.util.Arrays.asList("id1"));
        setField(rm, "requestDataUnitList", unitList);

        return rm;
    }

    private DatasyncJobConfiguration jobConfig(String metaObjectSysName) {
        DatasyncJobConfiguration cfg = new DatasyncJobConfiguration();
        setField(cfg, "id", UUID.randomUUID());
        setField(cfg, "metaObjectSysName", metaObjectSysName);
        setField(cfg, "systemName", "systemName_" + metaObjectSysName);
        setField(cfg, "sourceRegion", "AMER");
        setField(cfg, "execType", ExecType.REAL_TIME.name());
        return cfg;
    }

    private MetaObject metaObject(UUID id) {
        MetaObject mo = new MetaObject();
        // use reflection due to private field with Lombok
        try {
            var f = MetaObject.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(mo, id);
        } catch (Exception ignored) {}
        return mo;
    }

    private MetaObjectRelation relation(UUID childId, UUID parentId) {
        MetaObjectRelation rel = new MetaObjectRelation();
        MetaObject parent = metaObject(parentId);
        MetaObject child = metaObject(childId);
        try {
            var parentField = MetaObjectRelation.class.getDeclaredField("parentObject");
            parentField.setAccessible(true);
            parentField.set(rel, parent);
            var childField = MetaObjectRelation.class.getDeclaredField("childObject");
            childField.setAccessible(true);
            childField.set(rel, child);
        } catch (Exception ignored) {}
        return rel;
    }

    @Test
    @DisplayName("Direct job found for provided meta_object_id (no traversal)")
    void directJobFound() throws Exception {
        UUID metaId = UUID.randomUUID();
        String metaObjectSysName = "child_name";
        RequestModel request = buildRequest(metaId);

        DatasyncJobConfiguration cfg = jobConfig(metaObjectSysName);
        UUID cfgId = (UUID) getField(cfg, "id");
        when(datasyncJobConfigRepository.findByCustomerTenantSysNameAndSourceTenantIdAndMetaObjectSysNameAndExecType("customerTenantSysName", "sourceTenantId" ,metaObjectSysName, ExecType.REAL_TIME.name()))
                .thenReturn(Optional.of(cfg));
        when(datasyncJobConfigRepository.findDatasyncJobConfigurationById(cfgId))
                .thenReturn(Optional.of(cfg));
        when(metaObjectRepository.findById(metaId)).thenReturn(Optional.of(metaObject(metaId)));
        when(metaObjectRepository.findBySystemName(metaObjectSysName)).thenReturn(Optional.of(metaObject(metaId)));

        // job execution log stub
        when(jobExecutionLogRepository.save(any())).thenAnswer(inv -> {
            JobExecutionLog jel = inv.getArgument(0);
            try {
                setField(jel, "id", UUID.randomUUID());
                if (getField(jel, "jobExecutionId") == null) {
                    setField(jel, "jobExecutionId", UUID.randomUUID());
                }
            } catch (Exception ignored) {}
            return jel;
        });

        // Add valid object id for real time sync validation
        RequestBodyDataUnitList unitList = new RequestBodyDataUnitList();
        unitList.setObjIds(java.util.Arrays.asList("id1"));
        request.setRequestDataUnitList(unitList);

        String execId = service.publishDataSyncJob(request);
        assertNotNull(execId);
        verify(metaObjectRelationRepository, never()).findByChildObject_Id(any());
        verify(datasyncJobConfigRepository, times(1))
                .findByCustomerTenantSysNameAndSourceTenantIdAndMetaObjectSysNameAndExecType("customerTenantSysName", "sourceTenantId" ,metaObjectSysName, ExecType.REAL_TIME.name());
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(DatasyncMessage.class));
    }

    @Test
    @DisplayName("Fallback to root parent when no direct job exists (multi-level chain)")
    void fallbackToRootJob() throws Exception {
        // Create UUIDs for each ancestor
        UUID child = UUID.randomUUID();
        UUID parent = UUID.randomUUID();
        UUID grandParent = UUID.randomUUID();
        UUID root = UUID.randomUUID();

        // Create MetaObjects for each ancestor with correct system names
        MetaObject childMeta = metaObject(child);
        setField(childMeta, "systemName", "child_name");
        MetaObject parentMeta = metaObject(parent);
        setField(parentMeta, "systemName", "parent_name");
        MetaObject grandParentMeta = metaObject(grandParent);
        setField(grandParentMeta, "systemName", "grandparent_name");
        MetaObject rootMeta = metaObject(root);
        setField(rootMeta, "systemName", "root_name");

        // Setup metaObjectRepository.findById to return correct MetaObject for each UUID
        when(metaObjectRepository.findById(child)).thenReturn(Optional.of(childMeta));
        when(metaObjectRepository.findById(parent)).thenReturn(Optional.of(parentMeta));
        when(metaObjectRepository.findById(grandParent)).thenReturn(Optional.of(grandParentMeta));
        when(metaObjectRepository.findById(root)).thenReturn(Optional.of(rootMeta));
        // Also stub findBySystemName for each system name (for completeness, in case service uses it)
        when(metaObjectRepository.findBySystemName("child_name")).thenReturn(Optional.of(childMeta));
        when(metaObjectRepository.findBySystemName("parent_name")).thenReturn(Optional.of(parentMeta));
        when(metaObjectRepository.findBySystemName("grandparent_name")).thenReturn(Optional.of(grandParentMeta));

        // Setup datasyncJobConfigRepository to return empty for all except root
        when(datasyncJobConfigRepository.findByCustomerTenantSysNameAndSourceTenantIdAndMetaObjectSysNameAndExecType("customerTenantSysName", "sourceTenantId", "child_name", ExecType.REAL_TIME.name()))
                .thenReturn(Optional.empty());
        when(datasyncJobConfigRepository.findByCustomerTenantSysNameAndSourceTenantIdAndMetaObjectSysNameAndExecType("customerTenantSysName", "sourceTenantId", "parent_name", ExecType.REAL_TIME.name()))
                .thenReturn(Optional.empty());
        when(datasyncJobConfigRepository.findByCustomerTenantSysNameAndSourceTenantIdAndMetaObjectSysNameAndExecType("customerTenantSysName", "sourceTenantId", "grandparent_name", ExecType.REAL_TIME.name()))
                .thenReturn(Optional.empty());
        DatasyncJobConfiguration rootCfg = jobConfig("root_name");
        UUID rootCfgId = (UUID) getField(rootCfg, "id");
        when(datasyncJobConfigRepository.findByCustomerTenantSysNameAndSourceTenantIdAndMetaObjectSysNameAndExecType("customerTenantSysName", "sourceTenantId", "root_name", ExecType.REAL_TIME.name()))
                .thenReturn(Optional.of(rootCfg));
        when(datasyncJobConfigRepository.findDatasyncJobConfigurationById(rootCfgId))
                .thenReturn(Optional.of(rootCfg));

        // Setup parent relations
        when(metaObjectRelationRepository.findByChildObject_Id(child)).thenReturn(Optional.of(relation(child, parent)));
        when(metaObjectRelationRepository.findByChildObject_Id(parent)).thenReturn(Optional.of(relation(parent, grandParent)));
        when(metaObjectRelationRepository.findByChildObject_Id(grandParent)).thenReturn(Optional.of(relation(grandParent, root)));
        when(metaObjectRelationRepository.findByChildObject_Id(root)).thenReturn(Optional.empty());

        // Setup metaObjectRepository.findBySystemName for root_name
        when(metaObjectRepository.findBySystemName("root_name")).thenReturn(Optional.of(rootMeta));

        // job execution log stub
        when(jobExecutionLogRepository.save(any())).thenAnswer(inv -> {
            JobExecutionLog jel = inv.getArgument(0);
            try {
                setField(jel, "id", UUID.randomUUID());
                if (getField(jel, "jobExecutionId") == null) {
                    setField(jel, "jobExecutionId", UUID.randomUUID());
                }
            } catch (Exception ignored) {}
            return jel;
        });

        RequestModel request = buildRequest(child);
        String execId = service.publishDataSyncJob(request);
        assertNotNull(execId);

        // verify traversal sequence (stops once job found at root; no lookup performed for root itself)
        verify(metaObjectRelationRepository, times(1)).findByChildObject_Id(child);
        verify(metaObjectRelationRepository, times(1)).findByChildObject_Id(parent);
        verify(metaObjectRelationRepository, times(1)).findByChildObject_Id(grandParent);
        verify(metaObjectRelationRepository, never()).findByChildObject_Id(root);

        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(DatasyncMessage.class));
    }

    @Test
    @DisplayName("Traversal ends with no job at root -> error")
    void missingRootJob() {
        UUID child = UUID.randomUUID();
        UUID parent = UUID.randomUUID();
        UUID root = UUID.randomUUID();

        RequestModel request = buildRequest(child);

        when(datasyncJobConfigRepository.findByCustomerTenantSysNameAndSourceTenantIdAndMetaObjectSysNameAndExecType("customerTenantSysName", "sourceTenantId", "child_name", ExecType.REAL_TIME.name()))
                .thenReturn(Optional.empty());
        when(metaObjectRelationRepository.findByChildObject_Id(child))
                .thenReturn(Optional.of(relation(child, parent)));

        when(datasyncJobConfigRepository.findByCustomerTenantSysNameAndSourceTenantIdAndMetaObjectSysNameAndExecType("customerTenantSysName", "sourceTenantId", "parent_name", ExecType.REAL_TIME.name()))
                .thenReturn(Optional.empty());
        when(metaObjectRelationRepository.findByChildObject_Id(parent))
                .thenReturn(Optional.of(relation(parent, root)));

        when(datasyncJobConfigRepository.findByCustomerTenantSysNameAndSourceTenantIdAndMetaObjectSysNameAndExecType("customerTenantSysName", "sourceTenantId", "root_name", ExecType.REAL_TIME.name()))
                .thenReturn(Optional.empty());
        when(metaObjectRelationRepository.findByChildObject_Id(root))
                .thenReturn(Optional.empty());

        DataSyncJobException ex = assertThrows(DataSyncJobException.class,
                () -> service.publishDataSyncJob(request));
        assertFalse(ex.getMessage().isEmpty());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(DatasyncMessage.class));
    }
}
