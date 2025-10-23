package com.aditya.dataconnect.executionengine.pojo;//package com.thomsonreuters.dataconnect.executionengine.pojo;
//
//import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectDTO;
//import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectRelationDTO;
//import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.DatabaseVendor;
//import com.thomsonreuters.dataconnect.executionengine.model.pojo.AdaptorParams;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class AdaptorParamsTest {
//
//    @Test
//    void testGetterAndSetterMethods() {
//        AdaptorParams params = new AdaptorParams();
//
//        String dbUser = "testUser";
//        DatabaseVendor dbType = DatabaseVendor.ORACLE;
//        String dataSource = "testDataSource";
//        List<MetaObjectRelationDTO> metaObjectRelations = List.of(new MetaObjectRelationDTO());
//        MetaObjectDTO metaObject = new MetaObjectDTO();
//        List<MetaObjectDTO> parentMetaObject = List.of(new MetaObjectDTO());
//        List<MetaObjectDTO> childMetaObject = List.of(new MetaObjectDTO());
//        List<String> selectStatements = List.of("SELECT * FROM table");
//        String ssmKeyParameter = "testSSMKey";
//
//        params.setDbUser(dbUser);
//        params.setDbType(dbType);
//        params.setDataSource(dataSource);
//        params.setMetaObjectRelations((MetaObjectRelationDTO) metaObjectRelations);
//        params.setMetaObject(metaObject);
//        params.setParentMetaObject(parentMetaObject);
//        params.setChildMetaObject(childMetaObject);
//        params.setSelectStatements(selectStatements);
//        params.setSsmKeyParameter(ssmKeyParameter);
//
//        assertEquals(dbUser, params.getDbUser());
//        assertEquals(dbType, params.getDbType());
//        assertEquals(dataSource, params.getDataSource());
//        assertEquals(metaObjectRelations, params.getMetaObjectRelations());
//        assertEquals(metaObject, params.getMetaObject());
//        assertEquals(parentMetaObject, params.getParentMetaObject());
//        assertEquals(childMetaObject, params.getChildMetaObject());
//        assertEquals(selectStatements, params.getSelectStatements());
//        assertEquals(ssmKeyParameter, params.getSsmKeyParameter());
//    }
//
//    @Test
//    void testToStringMethod() {
//        AdaptorParams params = new AdaptorParams();
//
//        params.setDbUser("testUser");
//        params.setDbType(DatabaseVendor.ORACLE);
//        params.setDataSource("testDataSource");
//        params.setSsmKeyParameter("testSSMKey");
//
//        String expected = "AdaptorParams{db_user='testUser', db_type='ORACLE', data_source='testDataSource', SSM_key_parameter='testSSMKey', }";
//        assertEquals(expected, params.toString());
//    }
//}