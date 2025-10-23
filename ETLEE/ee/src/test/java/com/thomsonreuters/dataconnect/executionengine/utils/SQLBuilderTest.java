package com.thomsonreuters.dataconnect.executionengine.utils;

import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectAttributeDTO;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectDTO;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.DataType;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.PrimaryKeyInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SQLBuilderTest {

    private MetaObjectDTO metaObjectDTO;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        metaObjectDTO = new MetaObjectDTO();
        metaObjectDTO.setSchema("test_schema");
        metaObjectDTO.setDbTable("test_table");
        testDateTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
    }

    @Test
    public void testGetCreatedAt_withValidCreatedAttribute() throws DataSyncJobException {
        MetaObjectDTO dto = new MetaObjectDTO();

        MetaObjectAttributeDTO attr = new MetaObjectAttributeDTO();
        attr.setDisplayName("record_createdat");
        attr.setSystemName("record_createdat");
        attr.setDbColumn("record_created_column");
        attr.setDataType(DataType.TIMESTAMP);

        Set<MetaObjectAttributeDTO> attributes = new HashSet<>();
        attributes.add(attr);
        dto.setAttributes(attributes);

        String result = SQLBuilder.getCreatedAt(dto);
        assertEquals("record_created_column", result);
    }

    @Test
    public void testGetCreatedAt_withNoMatchingAttribute() throws DataSyncJobException {
        MetaObjectDTO dto = new MetaObjectDTO();

        MetaObjectAttributeDTO attr = new MetaObjectAttributeDTO();
        attr.setDisplayName("some_field");
        attr.setSystemName("some_field");
        attr.setDbColumn("some_column");
        attr.setDataType(DataType.STRING); // Not TIMESTAMP

        dto.setAttributes(Set.of(attr));

        String result = SQLBuilder.getCreatedAt(dto);
        assertNull(result); // Assuming method returns null if no match
    }

    @Test
    public void testIsTimestamp_withTimestampType() {
        MetaObjectAttributeDTO attr = new MetaObjectAttributeDTO();
        attr.setDataType(DataType.TIMESTAMP);

        boolean result = SQLBuilder.isTimestamp(attr);
        assertTrue(result);
    }

    // ============== TEST SCENARIOS FOR COALESCE FUNCTIONALITY ==============

    @Test
    @DisplayName("Test getSelectStatement when both updated_at and created_at are present - should use COALESCE")
    public void testGetSelectStatement_WhenBothTimestampColumnsPresent() throws DataSyncJobException {
        // Setup both updated_at and created_at attributes
        MetaObjectAttributeDTO updatedAtAttr = new MetaObjectAttributeDTO();
        updatedAtAttr.setDisplayName("updated_at");
        updatedAtAttr.setDbColumn("updated_at");
        updatedAtAttr.setDataType(DataType.TIMESTAMP);

        MetaObjectAttributeDTO createdAtAttr = new MetaObjectAttributeDTO();
        createdAtAttr.setDisplayName("created_at");
        createdAtAttr.setDbColumn("created_at");
        createdAtAttr.setDataType(DataType.TIMESTAMP);

        Set<MetaObjectAttributeDTO> attributes = new HashSet<>();
        attributes.add(updatedAtAttr);
        attributes.add(createdAtAttr);
        metaObjectDTO.setAttributes(attributes);

        // Execute
        String result = SQLBuilder.getSelectStatement(metaObjectDTO, testDateTime);

        // Verify - Should use COALESCE when both columns are present
        String expected = "SELECT * FROM test_schema.test_table WHERE COALESCE(updated_at, created_at) >= ?::timestamp without time zone";
        assertEquals(expected, result);
    }



    @Test
    @DisplayName("Test getUpdatedAt when no timestamp columns are present in attributes")
    public void testGetUpdatedAt_WhenNoTimestampColumnsPresent() throws DataSyncJobException {
        // Setup MetaObjectDTO with no timestamp columns
        MetaObjectAttributeDTO attr = new MetaObjectAttributeDTO();
        attr.setDisplayName("name");
        attr.setDbColumn("name");
        attr.setDataType(DataType.STRING);

        Set<MetaObjectAttributeDTO> attributes = new HashSet<>();
        attributes.add(attr);
        metaObjectDTO.setAttributes(attributes);

        // Execute
        String result = SQLBuilder.getUpdatedAt(metaObjectDTO);

        // Verify
        assertNull(result, "Should return null when no timestamp columns are present");
    }

    @Test
    @DisplayName("Test getCreatedAt when no timestamp columns are present in attributes")
    public void testGetCreatedAt_WhenNoTimestampColumnsPresent() throws DataSyncJobException {
        // Setup MetaObjectDTO with no timestamp columns
        MetaObjectAttributeDTO attr = new MetaObjectAttributeDTO();
        attr.setDisplayName("description");
        attr.setDbColumn("description");
        attr.setDataType(DataType.STRING);

        Set<MetaObjectAttributeDTO> attributes = new HashSet<>();
        attributes.add(attr);
        metaObjectDTO.setAttributes(attributes);

        // Execute
        String result = SQLBuilder.getCreatedAt(metaObjectDTO);

        // Verify
        assertNull(result, "Should return null when no timestamp columns are present");
    }

    // ============== TEST SCENARIOS FOR NULL UPDATED_AT COLUMN VALUES ==============

    @Test
    @DisplayName("Test getSelectStatement when updated_at column is present but null dbColumn")
    public void testGetSelectStatement_WhenUpdatedAtHasNullDbColumn() throws DataSyncJobException {
        // Setup updated_at attribute with null dbColumn
        MetaObjectAttributeDTO updatedAtAttr = new MetaObjectAttributeDTO();
        updatedAtAttr.setDisplayName("updated_at");
        updatedAtAttr.setDbColumn(null); // NULL dbColumn
        updatedAtAttr.setDataType(DataType.TIMESTAMP);

        // Setup created_at attribute as fallback
        MetaObjectAttributeDTO createdAtAttr = new MetaObjectAttributeDTO();
        createdAtAttr.setDisplayName("created_at");
        createdAtAttr.setDbColumn("created_at");
        createdAtAttr.setDataType(DataType.TIMESTAMP);

        Set<MetaObjectAttributeDTO> attributes = new HashSet<>();
        attributes.add(updatedAtAttr);
        attributes.add(createdAtAttr);
        metaObjectDTO.setAttributes(attributes);

        // Execute
        String result = SQLBuilder.getSelectStatement(metaObjectDTO, testDateTime);

        // Verify - Should fall back to created_at when updated_at dbColumn is null
        String expected = "SELECT * FROM test_schema.test_table WHERE created_at >= ?::timestamp without time zone";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test getUpdatedAt when updated_at attribute has null dbColumn")
    public void testGetUpdatedAt_WhenDbColumnIsNull() throws DataSyncJobException {
        // Setup updated_at attribute with null dbColumn
        MetaObjectAttributeDTO attr = new MetaObjectAttributeDTO();
        attr.setDisplayName("updated_at");
        attr.setDbColumn(null);
        attr.setDataType(DataType.TIMESTAMP);

        Set<MetaObjectAttributeDTO> attributes = new HashSet<>();
        attributes.add(attr);
        metaObjectDTO.setAttributes(attributes);

        // Execute
        String result = SQLBuilder.getUpdatedAt(metaObjectDTO);

        // Verify
        assertNull(result, "Should return null when dbColumn is null");
    }



    // ============== TEST SCENARIOS FOR UPDATEAT CONDITION CHECKING ==============

    @Test
    @DisplayName("Test edge case when both updated_at and created_at are missing but acceptedTime is provided")
    public void testGetSelectStatementForSpecificIds_WhenBothTimestampColumnsMissingWithAcceptedTime() throws DataSyncJobException {
        // Setup only non-timestamp attributes
        MetaObjectAttributeDTO pkAttr = new MetaObjectAttributeDTO();
        pkAttr.setDisplayName("id");
        pkAttr.setDbColumn("id");
        pkAttr.setDataType(DataType.BIGINT);
        pkAttr.setPrimary(true);

        Set<MetaObjectAttributeDTO> attributes = new HashSet<>();
        attributes.add(pkAttr);
        metaObjectDTO.setAttributes(attributes);
        metaObjectDTO.setUpdatedAt(null);

        // Setup primary key info
        PrimaryKeyInfo pkInfo = new PrimaryKeyInfo();
        pkInfo.setPkName("id");
        pkInfo.setComposite(false);

        List<String> objIds = Arrays.asList("1");
        LocalDateTime acceptedTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        // Execute
        String result = SQLBuilder.getSelectStatementForSpecificIds(
            metaObjectDTO, testDateTime, objIds, pkInfo, acceptedTime, "regular"
        );

        // Verify - Should only filter by objIds without timestamp condition
        String expected = "SELECT * FROM test_schema.test_table WHERE id IN (?)";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test getSelectStatement fallback behavior from updated_at to created_at")
    public void testGetSelectStatement_FallbackFromUpdatedAtToCreatedAt() throws DataSyncJobException {
        // Setup only created_at (no updated_at)
        MetaObjectAttributeDTO createdAtAttr = new MetaObjectAttributeDTO();
        createdAtAttr.setDisplayName("created_at");
        createdAtAttr.setDbColumn("created_at");
        createdAtAttr.setDataType(DataType.TIMESTAMP);

        Set<MetaObjectAttributeDTO> attributes = new HashSet<>();
        attributes.add(createdAtAttr);
        metaObjectDTO.setAttributes(attributes);

        // Execute
        String result = SQLBuilder.getSelectStatement(metaObjectDTO, testDateTime);

        // Verify - Should use created_at as fallback
        String expected = "SELECT * FROM test_schema.test_table WHERE created_at >= ?::timestamp without time zone";
        assertEquals(expected, result);
    }


    // ============== VALIDATION TESTS FOR DIFFERENT UPDATED_AT NAMING PATTERNS ==============

    @Test
    @DisplayName("Test getUpdatedAt with various naming patterns")
    public void testGetUpdatedAt_WithDifferentNamingPatterns() throws DataSyncJobException {
        // Test cases for different update column naming patterns
        String[] updateColumnNames = {
            "updated_at", "updatedat", "update_at", "updateAt",
            "update-at", "updated-at", "UPDATED_AT", "UpdatedAt"
        };

        for (String columnName : updateColumnNames) {
            // Setup fresh MetaObjectDTO for each test
            MetaObjectDTO testDto = new MetaObjectDTO();
            MetaObjectAttributeDTO attr = new MetaObjectAttributeDTO();
            attr.setDisplayName(columnName);
            attr.setDbColumn(columnName);
            attr.setDataType(DataType.TIMESTAMP);

            Set<MetaObjectAttributeDTO> attributes = new HashSet<>();
            attributes.add(attr);
            testDto.setAttributes(attributes);

            // Execute
            String result = SQLBuilder.getUpdatedAt(testDto);

            // Verify
            assertEquals(columnName, result,
                "Should find updated_at column with naming pattern: " + columnName);
        }
    }

    @Test
    @DisplayName("Test null/empty attributes handling")
    public void testNullAndEmptyAttributesHandling() throws DataSyncJobException {
        // Test null MetaObjectDTO
        assertNull(SQLBuilder.getUpdatedAt(null));
        assertNull(SQLBuilder.getCreatedAt(null));

        // Test MetaObjectDTO with null attributes
        MetaObjectDTO nullAttrDto = new MetaObjectDTO();
        nullAttrDto.setAttributes(null);
        assertNull(SQLBuilder.getUpdatedAt(nullAttrDto));
        assertNull(SQLBuilder.getCreatedAt(nullAttrDto));

        // Test MetaObjectDTO with empty attributes
        MetaObjectDTO emptyAttrDto = new MetaObjectDTO();
        emptyAttrDto.setAttributes(new HashSet<>());
        assertNull(SQLBuilder.getUpdatedAt(emptyAttrDto));
        assertNull(SQLBuilder.getCreatedAt(emptyAttrDto));
    }

    @Test
    public void testSelectCreatedAtUpdatedAtAndId() throws DataSyncJobException {
        // Set up MetaObjectDTO
        MetaObjectDTO metaObjectDTO = new MetaObjectDTO();
        metaObjectDTO.setSchema("public");
        metaObjectDTO.setDbTable("test_table");

        // Set up dataObjects
        List<ConcurrentHashMap<String, Object>> dataObjects = new ArrayList<>();
        ConcurrentHashMap<String, Object> row1 = new ConcurrentHashMap<>();
        row1.put("col1", 1);
        row1.put("col2", "A");
        dataObjects.add(row1);

        ConcurrentHashMap<String, Object> row2 = new ConcurrentHashMap<>();
        row2.put("col1", 2);
        row2.put("col2", "B");
        dataObjects.add(row2);

        // Set up ids
        List<String> ids = Arrays.asList("col1", "col2");

        // Call the method
        String result = SQLBuilder.selectCreatedAtUpdatedAtAndIds(metaObjectDTO, dataObjects, ids);

        // Expected SQL query
        String expected = "SELECT col1, col2 FROM public.test_table WHERE (col1, col2) IN ((1, 'A'), (2, 'B'))";

        // Assert the result
        assertEquals(expected, result);
    }
}
