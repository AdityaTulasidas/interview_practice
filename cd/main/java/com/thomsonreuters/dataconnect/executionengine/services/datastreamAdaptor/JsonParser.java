package com.thomsonreuters.dataconnect.executionengine.services.datastreamAdaptor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.DataUnit;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.DatasyncMessage;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.Header;
import com.thomsonreuters.dataconnect.executionengine.services.datastreamAdaptor.impl.DataUnitContent;
import com.thomsonreuters.dataconnect.executionengine.services.datastreamAdaptor.impl.ParserInterface;
import com.thomsonreuters.dataconnect.executionengine.utils.ConcurrentHashMapSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class JsonParser implements ParserInterface {

    private ObjectMapper objectMapper;

    public JsonParser() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public Header readHeader(InputStream inputStream) throws IOException {
        try {
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode headerNode = rootNode.path("header");
            // Validate and parse headerNode
            Header header = objectMapper.treeToValue(headerNode, Header.class);
            return header;
        } catch (IOException e) {
            // Handle exception and raise error
            throw new IOException("Failed to read and validate header", e);
        }
    }

    @Override
    public DataUnitContent readData(InputStream inputStream) throws IOException {
        try {
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode dataNode = rootNode.path("data");
            DataUnit dataunit = objectMapper.treeToValue(dataNode, DataUnit.class);
            return dataunit.getContent();
        } catch (IOException e) {
            throw new IOException("Failed to read and validate data", e);
        }
    }

    public void writeData(OutputStream outputStream, Header header, DataUnitContent dataUnitContent) throws IOException, DataSyncJobException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            objectMapper.writeValue(byteArrayOutputStream, header);
            objectMapper.writeValue(byteArrayOutputStream, dataUnitContent);
            outputStream.write(byteArrayOutputStream.toByteArray());
        } catch (JsonProcessingException e) {
            log.error("Error writing JSON data", e);
            throw new DataSyncJobException("Failed to write JSON data", "BAD_REQUEST");
        }
    }

    @Override
    public void writeData(ByteArrayOutputStream outputStream, DatasyncMessage datasyncMessage) throws DataSyncJobException, IOException {
        try {
            // Define the date format
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
            // Configure ObjectMapper
            this.objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            this.objectMapper.registerModule(javaTimeModule);
            this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            this.objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

            // Write the data
            objectMapper.writeValue(byteArrayOutputStream, datasyncMessage);
            outputStream.write(byteArrayOutputStream.toByteArray());
        } catch (JsonProcessingException e) {
            log.error("Error writing JSON data", e);
            throw new DataSyncJobException("Failed to write JSON data", "BAD_REQUEST");
        }
    }
}