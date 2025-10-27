package com.thomsonreuters.dataconnect.dataintegration.constant;

public class Constants {

    public static final String CUSTOMER_TENANT_SYS_NAME = "";

    private Constants() {
        // Private constructor to prevent instantiation
    }
    public static final String DATA_SOURCE_CREATED_SUCCESSFULLY = "Data Source Created Successfully with ID: ";
    public static final String ERROR_CREATING_DATA_SOURCE = "Error creating datasourcecatalog entry: ";
    public static final String DATA_STORE_ID_CANNOT_BE_NULL = "Data Store ID cannot be null";
    public static final String DATA_SOURCE_NOT_FOUND = "DataSourceCatalog not found with the given ID";
    public static final String DATA_SOURCE_UPDATED_SUCCESSFULLY = "Data Source Updated Successfully";
    public static final String ERROR_UPDATING_DATA_SOURCE = "An error occurred while updating the data source catalog entry";
    public static final String NO_DATA_SOURCE_ENTRIES_FOUND = "No DataSourceCatalog entries found";
    public static final String ERROR_RETRIEVING_DATA_SOURCE_ENTRIES = "An error occurred while retrieving data source catalog entries";
    public static final String DATA_SOURCE_RETRIVAL_SUCCESSFULLY = "Successful retrieval of all DataSourceCatalog entries";
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String OK = "OK";
    public static final String CUSTOM_MESSAGE = "CUSTOM MESSAGE";
    public static final String CONFLICT = "CONFLICT";
    public static final String ERROR = "error";
    public static final String MESSAGE = "message";
    public static final String CODE = "code";
    public static final String API_HEADER_CREATE = "Create a new data source catalog entry";
    public static final String API_HEADER_GET_BY_ID = "Get data source catalog entries with given ID";
    public static final String API_HEADER_ONESOURCE_JOB_CREATION_SUCCESS = "Successful creation of onesource data sync job";
    public static final String API_HEADER_ONESOURCE_JOB_UPDATION_SUCCESS = "Successfully updated onesource data sync job";
    public static final String API_HEADER_ONESOURCE_JOB_EXECUTION_SUCCESS = "Successful creation of onesource data sync job";
    public static final String API_HEADER_NO_CONTENT = "No Content";
    public static final String API_HEADER_NOT_FOUND = "DataSourceCatalog not found";
    public static final String API_HEADER_BAD_REQUEST = "Error processing the argument 'job_id'. No property found ";
    public static final String API_HEADER_SERVER_ERROR = "Internal Server Error";
    public static final String API_HEADER_CONTENT_TEXT = "text/plain";
    public static final String API_HEADER_CONTENT_JSON = "application/json";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String ENUM_VALIDATION_ERROR = "One or more enum validation errors occurred.";
    public static final String VALIDATION_ERROR = "One or more validation errors occurred.";
    public static final Integer TENANT_COUNT=1;

    public static final String DATASYNC_JOB_SUCCESSFUL_FETCH = "DataSync Job retrieved successfully";
    public static final String API_UNAUTHORIZED = "Unauthorized";
    public static final String DATASYNC_JOB_BAD_REQUEST = "INVALID_REQUEST";
    public static final String DATASYNC_JOB_NOT_FOUND = "DataSync Job not found";
    public static final String METAOBJECT_JOB_NOT_FOUND = "The meta object linked to the job could not be found";
    public static final String JOB_CONFIG_CREATED_SUCCESSFULLY = "Job configuration created with id : ";
    public static final String JOB_CONFIG_UPDATED_SUCCESSFULLY = "Job configuration updated with id : ";

    public static final String JOB_EXECUTION_SUCCESS = "Job is executed with id : ";
    public static final String INVALID_OPERATION_TYPE = "Invalid operation type";
    public static final String INVALID_REQUEST_BODY = "No valid request body provided";
    public static final String META_OBJECT_NOT_FOUND= "Meta object not found with the given ID";
    public static final String SYSTEM = "system";
    public static final String DUPLICATE_JOB_RUNNING = "A job with ID %s is already running with status: %s";
    // LogRecord related constants
    public static final String LOG_TYPE_ACTIVITY = "Activity";
    public static final String LOG_COMPONENT_DATA_SYNC_SERVICE = "DataSyncService";
    public static final String LOG_COMPONENT_JOB_CONFIG_CONTROLLER = "JobConfigurationController";
    public static final String LOG_COMPONENT_DATA_INTEGRATION_MANAGER_CONTROLLER = "DataIntegrationManagerController";
    public static final String LOG_COMPONENT_GLOBAL_EXCEPTION_HANDLER = "GlobalExceptionHandler";
    public static final String LOG_CORRELATION_ID = "1";
    public static final String LOG_REGION_EMEA = "EMEA";
    public static final String LOG_TYPE_ACTIVITY_FAILURE = "Activity Failure";
    public static final String LOG_COMPONENT_DATA_INTEGRATION_SERVICE = "DataIntegration Service";

    // Log message constants
    public static final String ERROR_CREATING_META_OBJECT = "Error while creating meta object from result set";
    public static final String BEAN_RESTTEMPLATE_LOADED = "Bean 'RestTemplate' loaded.";

    public static final String RequestModel_null = "Request Model is null";
    public static final String OperationType_null = "Operation Type is null";
    public static final String JobConfig_request_created = "Job configuration request created: ";
    public static final String SERVICE_NAME = "dataconnect_dataintegration";
    public static final String JSON_PROCESSING_EXCEPTION = "Failed to serialize JobExecutionLog to JSON";
    public static final String CUSTOMER_TENANT_API_PATH = "/api/v1/customer-tenants/";
    public static final String ONESOURCE_DATASYNC_SYS_NAME = "system.platform.dataconnect.onesource-datasync-job";
    public static final String OBJECT_ID_REQUIRED = "Atleast one object id should be provided for real time sync";
    public static final String ATLEAST_TWO_COLUMN_REQUIRED = "Atleast two column details should be provided for real time sync";
    public static final String ALL_COLUMNS_MUST_HAVE_SAME_NUMBER_OF_IDS = "All columns must have same number of the offset ids";
    public static final String DUPLICATE_KEY_FOUND = "Duplicate key found: ";
    public static final String MAP_WITH_SINGLE_KEY_REQUIRED = "Each Composite PK map should have a single key";
    public static final String OBJECT_IDS_SHOULD_BE_EMPTY_FOR_BATCH_SYNC = "Object ids should be empty for batch sync";
}
