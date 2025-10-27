package com.thomsonreuters.dataconnect.executionengine.services.datastreamAdaptor.impl;


import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.DatasyncMessage;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.Header;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ParserInterface {
    Header readHeader(InputStream inputStream) throws IOException;
    DataUnitContent readData(InputStream inputStream) throws IOException;
    void writeData(ByteArrayOutputStream outputStream, DatasyncMessage datasyncMessage) throws DataSyncJobException,IOException ;
}