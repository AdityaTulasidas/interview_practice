package com.thomsonreuters.dataconnect.executionengine.model.pojo;


import lombok.Data;

@Data
public class DatasyncMessage {
    private Header header;
    private DataUnit data;
}
