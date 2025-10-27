package com.thomsonreuters.dataconnect.dataintegration.model.pojo;


import lombok.Data;

@Data
public class DatasyncMessage {
    private Header header;
    private DataUnit data;
}