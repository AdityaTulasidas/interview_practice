package com.thomsonreuters.dataconnect.executionengine.adapters.impl;

import com.thomsonreuters.dataconnect.common.executioncontext.AdapterContext;
import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.thomsonreuters.dataconnect.common.executioncontext.RegionalJobContext;
import com.thomsonreuters.dataconnect.executionengine.adapters.CommonAdaptersUtil;
import com.thomsonreuters.dataconnect.executionengine.adapters.IntegrationAdapter;
import com.thomsonreuters.dataconnect.executionengine.data.DataRow;
import com.thomsonreuters.dataconnect.executionengine.data.DataSet;
import com.thomsonreuters.dataconnect.executionengine.data.DataSetCollection;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectAttributeDTO;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectDTO;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.entity.MetaObject;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.DataUnit;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.DataUnitFileObject;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.DatasyncMessage;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.Header;
import com.thomsonreuters.dataconnect.executionengine.repository.MetaObjectRepository;
import com.thomsonreuters.dataconnect.executionengine.services.awsservices.AwsS3Services;
import com.thomsonreuters.dataconnect.executionengine.services.fileadaptor.FileCsvParser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileAdapter implements IntegrationAdapter {

    @Autowired
    private CommonAdaptersUtil commonAdaptersUtil;
    @Autowired
    private AwsS3Services awsS3Services;
    @Autowired
    private FileCsvParser fileCsvParser;
    @Autowired
    private MetaObjectRepository metaObjectRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    @Qualifier("fileTargetsRabbitMQTemplates")
    private Map<String, RabbitTemplate> fileTargetsRabbitMQTemplates;

    @Autowired
    @Qualifier("fileTargetsRabbitMQExchanges")
    private Map<String, String> fileTargetsRabbitMQExchanges;

    @Autowired
    @Qualifier("fileTargetsRabbitMQRoutingKeys")
    private Map<String, String> fileTargetsRabbitMQRoutingKeys;

    private static final String DATA_UNIT = "data_unit";

    @Override
    public void validate() {

    }

    @Override
    public DataSetCollection readData(ExecutionContext ctx) throws DataSyncJobException {
        List<DataSet> dataSets = new ArrayList<>();
        DataUnitFileObject dataUnitFileObject = null;
        RegionalJobContext regionalJobContext = (RegionalJobContext) ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT);
        UUID jobId = regionalJobContext.getValue(RegionalJobContext.JOB_ID);
        AdapterContext adapterContext = (AdapterContext) ctx.getContextByName(ExecutionContext.IN_ADAPTER_CONTEXT);
        DataUnit data = adapterContext.getValue(DATA_UNIT);
        if (data.getContent() instanceof DataUnitFileObject) {
            dataUnitFileObject = (DataUnitFileObject) data.getContent();
        } else {
            throw new DataSyncJobException("DataUnit content is not of type DataSetCollection." + jobId.toString(), "BAD_REQUEST");
        }
        if (dataUnitFileObject != null && dataUnitFileObject.getFileList() != null && !dataUnitFileObject.getFileList().isEmpty()) {
            List<String> fileNames = dataUnitFileObject.getFileList();
            for (String fileName : fileNames) {
                DataSet dataSet = new DataSet();
                String fileNameWithPath = dataUnitFileObject.getFolder() + "/" + fileName;
                byte[] fileContents = awsS3Services.retrieveFileContentsAsBytes(fileNameWithPath);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileContents);
                String metaObjectId = fileName.substring(fileName.lastIndexOf("/")+1, fileName.indexOf(".csv"));
                MetaObject metaObject = metaObjectRepository.findMetaObjectById(UUID.fromString(metaObjectId));
                if (metaObject == null) {
                    throw new DataSyncJobException("MetaObject not found for id: " + metaObjectId + "." + jobId.toString(), "NOT_FOUND");
                }
                MetaObjectDTO metaObjectDTO = modelMapper.map(metaObject,MetaObjectDTO.class);

                dataSet.setHierarchyIndex(Integer.parseInt(fileName.substring(0, fileName.indexOf('/'))));
                dataSet.setMetaObject(metaObjectDTO);
                dataSet.setDataRows(fileCsvParser.readDataFromFile(byteArrayInputStream,",", metaObjectDTO));
                dataSets.add(dataSet);
            }
        } else {
            throw new DataSyncJobException("DataSetCollection is null in DataUnitFileObject." + jobId.toString(), "BAD_REQUEST");
        }
        DataSetCollection dataSetCollection = new DataSetCollection();
        dataSetCollection.setDataSets(dataSets);
        return dataSetCollection;
    }

    @Override
    public void writeData(DataSetCollection data, ExecutionContext ctx) throws DataSyncJobException {
        RegionalJobContext regionalJobContext = (RegionalJobContext) ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT);
        String jobName = regionalJobContext.getValue(RegionalJobContext.JOB_NAME);
        try {
            String targetRegions = regionalJobContext.getValue(RegionalJobContext.TARGET_REGIONS);
            List<String> fileNames = new ArrayList<>();
            List<DataSet> sortedDataSets = data.getDataSets().stream()
                    .sorted(Comparator.comparingInt(DataSet::getHierarchyIndex))
                    .toList();
            String folderPath = createFolderPath(ctx);
            for (DataSet dataSet : sortedDataSets) {
                MetaObjectDTO metaObject = dataSet.getMetaObject();
                if (metaObject == null) {
                    throw new DataSyncJobException("MetaObject is null in DataSet for job: "+ jobName, "BAD_REQUEST");
                }
                Set<MetaObjectAttributeDTO> metaAttribute = metaObject.getAttributes();
                if (metaAttribute == null || metaAttribute.isEmpty()) {
                    throw new DataSyncJobException("MetaObject attributes are null or empty in DataSet for job: "+jobName, "BAD_REQUEST");
                }
                List<MetaObjectAttributeDTO> sortedMetaAttribute = metaAttribute.stream()
                        .sorted(Comparator.comparingInt(MetaObjectAttributeDTO::getSeqNum))
                        .collect(Collectors.toList());
                String fileName = createFileName(metaObject.getSystemName(), metaObject.getId().toString(), dataSet.getHierarchyIndex());
                String fileNameWithPath = folderPath + "/" + fileName;
                InputStream inputStream = getInputStream(dataSet, fileName, sortedMetaAttribute, fileNames, fileNameWithPath);
                awsS3Services.writeToS3(fileNameWithPath, targetRegions, inputStream);
            }
            publishMessageToQueue(folderPath, fileNames, targetRegions,
                    regionalJobContext.getValue(RegionalJobContext.JOB_NAME), ctx);
        } catch (Exception e) {
            throw new DataSyncJobException("Error writing data to file for job: " +jobName , "INTERNAL_SERVER_ERROR");
        }
    }

    private InputStream getInputStream(DataSet dataSet, String fileName, List<MetaObjectAttributeDTO> sortedMetaAttribute, List<String> fileNames, String fileNameWithPath) throws DataSyncJobException, IOException {
        Path resourcePath = Paths.get(fileNameWithPath);
        Files.createDirectories(resourcePath.getParent()); // Ensure the directory exists
        InputStream inputStream = null;
        try  {
            List<LinkedHashMap<String,Object>> dataObjects = new ArrayList<>();
            if (dataSet.getDataRows() != null) {
                for (DataRow dataRow : dataSet.getDataRows()) {
                    // Write data rows
                    LinkedHashMap<String, Object> dataObjectMap = new LinkedHashMap<>();
                    for (int i = 0; i < sortedMetaAttribute.size(); i++) {
                        MetaObjectAttributeDTO attribute = sortedMetaAttribute.get(i);
                        Object fieldValueObj = dataRow.getRow().get(attribute.getDbColumn());
                        Object fieldValue = (fieldValueObj instanceof LocalDateTime)
                                ? fieldValueObj.toString()
                                : fieldValueObj;
                        dataObjectMap.put(attribute.getDbColumn(), fieldValue);
                    }
                    dataObjects.add(dataObjectMap);
                }
            }
            inputStream = mapListToInputStream(dataObjects);
            fileNames.add(fileName);

            log.info("Data successfully written to file: {}", fileName);
        } catch (Exception e) {
            throw new DataSyncJobException("Error writing to file: " + fileName, "INTERNAL_SERVER_ERROR");

        }
        return inputStream;
    }

    /**
     * Converts a list of LinkedHashMap<String, Object> (dataObjects) to an InputStream in CSV format.
     * Each map represents a row, keys are used as headers.
     */
    private InputStream mapListToInputStream(List<LinkedHashMap<String, Object>> dataObjects) {
        if (dataObjects == null || dataObjects.isEmpty()) {
            return new ByteArrayInputStream(new byte[0]);
        }
        StringBuilder sb = new StringBuilder();
        // Write rows
        for (LinkedHashMap<String, Object> row : dataObjects) {
            sb.append(row.values().stream()
                .map(value -> value == null ? "": value.toString().replace(",", "\\|")) // Escape commas
                .collect(Collectors.joining(",")))
              .append("\n");
        }
        return new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void publishMessageToQueue(String folderPath, List<String> fileNames, String targetRegions, String jobName, ExecutionContext ctx) {
        Header header = commonAdaptersUtil.generateHeader((RegionalJobContext) ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT));
        DataUnit dataUnit = new DataUnit();
        DataUnitFileObject dataUnitFileObject = new DataUnitFileObject();
        dataUnitFileObject.setFolder(folderPath);
        dataUnitFileObject.setFileList(fileNames);
        dataUnit.setContent(dataUnitFileObject);

        String[] targets = targetRegions.split(",");
        for (String target : targets) {
            target = target.trim();
            DatasyncMessage message = new DatasyncMessage();
            message.setHeader(header);
            message.setData(dataUnit);

            try {
                RabbitTemplate targetRabbitMQTemplate = fileTargetsRabbitMQTemplates.get(target);
                targetRabbitMQTemplate.convertAndSend(fileTargetsRabbitMQExchanges.get(target),
                        fileTargetsRabbitMQRoutingKeys.get(target),
                        message);
                log.info("Message sent to target {} queue for regional job: {}", target, jobName);
            } catch (Exception e) {
                log.error("Failed to send message to target {} queue for regional job: {} with error: {}",
                        target, jobName, e.getMessage(), e);
            }
        }
    }


    public String createFolderPath(ExecutionContext ctx) throws DataSyncJobException {
        RegionalJobContext regionalJobContext = (RegionalJobContext) ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT);
        if (regionalJobContext == null ||
                regionalJobContext.getValue(RegionalJobContext.JOB_NAME) == null ||
                regionalJobContext.getValue(RegionalJobContext.ONESOURCE_DOMAIN) == null ||
                regionalJobContext.getValue(RegionalJobContext.EXEC_ID) == null ||
                regionalJobContext.getValue(RegionalJobContext.SOURCE_REGION) == null) {
            throw new DataSyncJobException("Regional Job Context details in the Execution Context", "BAD_REQUEST");
        }

        StringBuilder folderPath = new StringBuilder();
        folderPath.append("input/")
                .append(regionalJobContext.getValue(RegionalJobContext.SOURCE_REGION).toString())
                .append("/")
                .append(regionalJobContext.getValue(RegionalJobContext.ONESOURCE_DOMAIN).toString())
                .append("/")
                .append(regionalJobContext.getValue(RegionalJobContext.JOB_NAME).toString())
                .append("/")
                .append(regionalJobContext.getValue(RegionalJobContext.EXEC_ID).toString());
        return folderPath.toString();
    }

    public String createFileName(String metaObjectName, String metaObjectId, int hierarchyIndex) {
        StringBuilder fileName = new StringBuilder();
        fileName.append(hierarchyIndex)
                .append("/")
                .append(metaObjectName)
                .append("/")
                .append(metaObjectId)
                .append(".csv");
        return fileName.toString();
    }
    @Override
    public void cleanUp() {
    }
}
