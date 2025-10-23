package com.thomsonreuters.dataconnect.executionengine.service.fileadaptor;

import com.thomsonreuters.dataconnect.executionengine.adapters.CommonAdaptersUtil;
import com.thomsonreuters.dataconnect.executionengine.data.DataRow;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectDTO;
import com.thomsonreuters.dataconnect.executionengine.repository.MetaObjectRelationRepository;
import com.thomsonreuters.dataconnect.executionengine.repository.MetaObjectRepository;
import com.thomsonreuters.dataconnect.executionengine.services.MetaModelClient;
import com.thomsonreuters.dataconnect.executionengine.services.fileadaptor.FileCsvParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class FileCsvParserTest {

    private FileCsvParser fileCsvParser;

    @Mock
    private MetaModelClient metaModelClient;

    @Mock
    private MetaObjectRelationRepository metaObjectRelationRepository;

    @Mock
    private MetaObjectRepository metaObjectRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CommonAdaptersUtil commonAdaptersUtil;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        fileCsvParser = new FileCsvParser(metaModelClient, metaObjectRelationRepository, metaObjectRepository, modelMapper, commonAdaptersUtil);
    }

    @Test
    void shouldReadDataFromFileSuccessfully() throws Exception {
        String csv = "id,name\n1,John\n2,Jane";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csv.getBytes());
        MetaObjectDTO metaObjectDTO = mock(MetaObjectDTO.class);
        when(metaObjectDTO.getAttributes()).thenReturn(new HashSet<>());
        List<DataRow> result = fileCsvParser.readDataFromFile(inputStream, ",", metaObjectDTO);
        assertNotNull(result);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }
}
