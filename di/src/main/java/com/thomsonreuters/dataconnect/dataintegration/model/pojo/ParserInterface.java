package com.thomsonreuters.dataconnect.dataintegration.model.pojo;



import java.io.IOException;
import java.io.InputStream;

public interface ParserInterface {
    Header readHeader(InputStream inputStream) throws IOException;
    DataUnitContent readData(InputStream inputStream) throws IOException;

}