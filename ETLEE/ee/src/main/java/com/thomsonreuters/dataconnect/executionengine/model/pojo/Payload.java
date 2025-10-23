package com.thomsonreuters.dataconnect.executionengine.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payload {


        private String action;
        private String tenantId;
        private String clientId;
        private String correlationId;
        private String schemaId;
        private String key;
        private String username;
        private Object data;

}
