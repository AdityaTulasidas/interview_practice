package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.DatasyncJobConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public
class DatasyncJobConfigurationSearchResponseDTOTest {

    @Test
    void shouldSetAllFieldsCorrectly_WhenDTOIsInitializedWithValidData() {
        DatasyncJobConfiguration jobConfiguration = new DatasyncJobConfiguration();
        jobConfiguration.setId(UUID.randomUUID());
        jobConfiguration.setSystemName("Test Job");

        PageRequest pageable = PageRequest.of(0, 10, Sort.by("name"));
        DatasyncJobConfigurationSearchResponseDTO responseDTO = new DatasyncJobConfigurationSearchResponseDTO(
                Collections.singletonList(jobConfiguration),
                pageable,
                1,
                1,
                true,
                10,
                0,
                Sort.by("name"),
                1,
                true,
                false
        );

        assertEquals(1, responseDTO.getTotalElements());
        assertEquals(1, responseDTO.getTotalPages());
        assertTrue(responseDTO.isLast());
        assertEquals(10, responseDTO.getSize());
        assertEquals(0, responseDTO.getNumber());
        assertEquals(Sort.by("name"), responseDTO.getSort());
        assertEquals(1, responseDTO.getNumberOfElements());
        assertTrue(responseDTO.isFirst());
        assertFalse(responseDTO.isEmpty());
        assertEquals("Test Job", responseDTO.getContent().get(0).getSystemName());
    }

    @Test
    void shouldReturnEmptyResponse_WhenDTOIsInitializedWithNoData() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("name"));
        DatasyncJobConfigurationSearchResponseDTO responseDTO = new DatasyncJobConfigurationSearchResponseDTO(
                Collections.emptyList(),
                pageable,
                0,
                0,
                true,
                10,
                0,
                Sort.by("name"),
                0,
                true,
                true
        );

        assertEquals(0, responseDTO.getTotalElements());
        assertEquals(0, responseDTO.getTotalPages());
        assertTrue(responseDTO.isLast());
        assertEquals(10, responseDTO.getSize());
        assertEquals(0, responseDTO.getNumber());
        assertEquals(Sort.by("name"), responseDTO.getSort());
        assertEquals(0, responseDTO.getNumberOfElements());
        assertTrue(responseDTO.isFirst());
        assertTrue(responseDTO.isEmpty());
    }
}