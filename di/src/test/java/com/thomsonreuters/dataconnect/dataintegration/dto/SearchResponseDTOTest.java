package com.thomsonreuters.dataconnect.dataintegration.dto;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

public class SearchResponseDTOTest {

    @Test
    void shouldReturnItemsSuccessfully_WhenItemsAreSet() {
        SearchResponseDTO<String> responseDTO = new SearchResponseDTO<>();
        List<String> items = Arrays.asList("item1", "item2");
        responseDTO.setItems(items);
        assertEquals(items, responseDTO.getItems());
    }

    @Test
    void shouldSetItemsSuccessfully_WhenValidItemsAreProvided() {
        SearchResponseDTO<String> responseDTO = new SearchResponseDTO<>();
        List<String> items = Arrays.asList("item1", "item2");
        responseDTO.setItems(items);
        assertEquals(items, responseDTO.getItems());
    }

    @Test
    void shouldReturnMetaSuccessfully_WhenMetaIsSet() {
        SearchResponseDTO<String> responseDTO = new SearchResponseDTO<>();
        SearchResponseDTO.MetaData metaData = new SearchResponseDTO.MetaData(10, 5, 1);
        responseDTO.setMeta(metaData);
        assertEquals(metaData, responseDTO.getMeta());
    }

    @Test
    void shouldSetMetaSuccessfully_WhenValidMetaIsProvided() {
        SearchResponseDTO<String> responseDTO = new SearchResponseDTO<>();
        SearchResponseDTO.MetaData metaData = new SearchResponseDTO.MetaData(10, 5, 1);
        responseDTO.setMeta(metaData);
        assertEquals(metaData, responseDTO.getMeta());
    }

    @Test
    void shouldCompareMetaDataSuccessfully_WhenMetaDataObjectsAreEqual() {
        SearchResponseDTO.MetaData metaData1 = new SearchResponseDTO.MetaData(10, 5, 1);
        SearchResponseDTO.MetaData metaData2 = new SearchResponseDTO.MetaData(10, 5, 1);
        assertEquals(metaData1.getCount(), metaData2.getCount());
        assertEquals(metaData1.getLimit(), metaData2.getLimit());
        assertEquals(metaData1.getOffset(), metaData2.getOffset());
    }

    @Test
    void shouldCompareSearchResponseDTOItemsSuccessfully_WhenItemsAreEqual() {
        SearchResponseDTO<String> responseDTO1 = new SearchResponseDTO<>();
        responseDTO1.setItems(Collections.singletonList("item1"));
        responseDTO1.setMeta(new SearchResponseDTO.MetaData(10, 5, 1));

        SearchResponseDTO<String> responseDTO2 = new SearchResponseDTO<>();
        responseDTO2.setItems(Collections.singletonList("item1"));
        responseDTO2.setMeta(new SearchResponseDTO.MetaData(10, 5, 1));

        assertLinesMatch(responseDTO1.getItems(), responseDTO2.getItems());
    }

}