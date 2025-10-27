package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.DatasyncJobConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DatasyncJobConfigurationSearchResponseDTO {

    private List<DatasyncJobConfiguration> content;
    private Pageable pageable;
    private int totalElements;
    private int totalPages;
    private boolean last;
    private int size;
    private int number;
    private Sort sort;
    private int numberOfElements;
    private boolean first;
    private boolean empty;
}
