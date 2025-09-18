package com.aditya.ETLExecutionEngine.data;

import lombok.Data;

@Data
public class DatasyncMessage {
    private Header header;
    private DataUnit data;
}