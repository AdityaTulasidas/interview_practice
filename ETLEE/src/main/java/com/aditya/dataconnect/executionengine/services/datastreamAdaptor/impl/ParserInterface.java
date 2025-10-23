package com.aditya.dataconnect.executionengine.services.datastreamAdaptor.impl;


import com.aditya.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.aditya.dataconnect.executionengine.model.pojo.DatasyncMessage;
import com.aditya.dataconnect.executionengine.model.pojo.Header;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface ParserInterface {
    Header readHeader(InputStream inputStream) throws IOException;
    DataUnitContent readData(InputStream inputStream) throws IOException;
    void writeData(ByteArrayOutputStream outputStream, DatasyncMessage datasyncMessage) throws DataSyncJobException,IOException ;
}