package com.thomsonreuters.dataconnect.executionengine.model.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Params {
    private String name;
    private String value;

    @JsonCreator
    public Params(@JsonProperty("name") String name, @JsonProperty("value") String value ){
        this.name = name;
        this.value = value;
    }
}