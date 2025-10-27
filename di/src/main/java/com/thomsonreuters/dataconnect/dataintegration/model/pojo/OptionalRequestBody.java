package com.thomsonreuters.dataconnect.dataintegration.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionalRequestBody {

    @JsonProperty("data_unit_list")
    @Valid
    @NotNull
    private RequestBodyDataUnitList requestDataUnitList;
    @JsonProperty("data_unit_object")
    private RequestBodyDataUnitObject requestDataUnitObject;

}