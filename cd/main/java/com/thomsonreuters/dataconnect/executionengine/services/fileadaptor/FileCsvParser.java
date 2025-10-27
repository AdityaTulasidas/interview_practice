package com.thomsonreuters.dataconnect.executionengine.services.fileadaptor;

import com.thomsonreuters.dataconnect.executionengine.adapters.CommonAdaptersUtil;
import com.thomsonreuters.dataconnect.executionengine.constant.Constants;
import com.thomsonreuters.dataconnect.executionengine.data.DataRow;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectAttributeDTO;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectDTO;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.DatasyncMessage;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.Header;
import com.thomsonreuters.dataconnect.executionengine.repository.MetaObjectRelationRepository;
import com.thomsonreuters.dataconnect.executionengine.repository.MetaObjectRepository;
import com.thomsonreuters.dataconnect.executionengine.services.MetaModelClient;
import com.thomsonreuters.dataconnect.executionengine.services.datastreamAdaptor.impl.DataUnitContent;
import com.thomsonreuters.dataconnect.executionengine.services.datastreamAdaptor.impl.ParserInterface;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Slf4j
@Component
public class FileCsvParser implements ParserInterface {

    private final MetaModelClient metaModelClient;

    private final MetaObjectRelationRepository metaObjectRelationRepository;

    private final MetaObjectRepository metaObjectRepository;

    private final ModelMapper modelMapper;

    private final CommonAdaptersUtil commonAdaptersUtil;

    @Autowired
    public FileCsvParser(MetaModelClient metaModelClient, MetaObjectRelationRepository metaObjectRelationRepository, MetaObjectRepository metaObjectRepository, ModelMapper modelMapper, CommonAdaptersUtil commonAdaptersUtil) {
        this.metaModelClient = metaModelClient;
        this.metaObjectRelationRepository = metaObjectRelationRepository;
        this.metaObjectRepository = metaObjectRepository;
        this.modelMapper = modelMapper;
        this.commonAdaptersUtil = commonAdaptersUtil;
    }

    @Override
    public Header readHeader(InputStream inputStream) throws IOException {
        return null;
    }

    @Override
    public DataUnitContent readData(InputStream inputStream) throws IOException {
        return null;
    }

    @Override
    public void writeData(ByteArrayOutputStream outputStream, DatasyncMessage datasyncMessage) throws DataSyncJobException, IOException {
        // Need to implement this method when required in the future
    }

    public List<DataRow> readDataFromFile(ByteArrayInputStream inputStream, String delimiter,MetaObjectDTO metaObject) throws DataSyncJobException {
        log.info("Starting readData process with delimiter: {}", delimiter);
        String line;
        int lineNumber = 0;
        List<DataRow> dataRows = new ArrayList<>();
        try {
            // Initialize data structures for parent and child data
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(streamReader);
            while ((line = reader.readLine()) != null) {
                DataRow dataRow = new DataRow();
                lineNumber++;
                // Check for empty lines
                if (line.trim().isEmpty()) {
                    log.error("Empty line detected at line number: {}", lineNumber);
                    throw new DataSyncJobException("CSV file contains an empty line at line number: " + lineNumber, Constants.INTERNAL_SERVER_ERROR);
                }

                // Split the line using a regex to respect escaped commas
                String[] fields = line.split(delimiter);
                // Replace escaped commas with actual commas in the fields
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].replace("\\|", ",");
                }
                dataRow.setRow(populateDataObject(metaObject, fields));
                dataRows.add(dataRow);
            }
        } catch (Exception e) {
            log.error("Exception occurred while reading data: {}", e.getMessage());
            throw new DataSyncJobException("Error reading CSV data: " + metaObject.getId().toString(), Constants.INTERNAL_SERVER_ERROR);
        }
        return dataRows;
    }

    public ConcurrentHashMap<String, Object> populateDataObject(MetaObjectDTO parentMetaObjectDTO, String[] fields) {
        Set<MetaObjectAttributeDTO> sortedMetaObjects = parentMetaObjectDTO.getAttributes();
        List<MetaObjectAttributeDTO> sortedMetaObjectsBySeqNum = sortMetaObjectsBySeqNum(sortedMetaObjects);
        ConcurrentHashMap<String, Object> dataObject = new ConcurrentHashMap<>();
        // Iterate over sorted meta objects and fields[]
        for (int i = 0; i < sortedMetaObjectsBySeqNum.size() && i < fields.length; i++) {
            MetaObjectAttributeDTO metaRelation = sortedMetaObjectsBySeqNum.get(i);
            String fieldValue = fields[i];
            dataObject.put(metaRelation.getDbColumn(), fieldValue);
        }
        return dataObject;
    }

    public List<MetaObjectAttributeDTO> sortMetaObjectsBySeqNum(Set<MetaObjectAttributeDTO> metaObjects) {
        // Convert the Set to a List and sort by seqNum
        return metaObjects.stream().sorted(Comparator.comparingInt(MetaObjectAttributeDTO::getSeqNum)).collect(Collectors.toList());
    }

}
