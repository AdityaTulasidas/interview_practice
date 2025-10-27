package com.thomsonreuters.dataconnect.executionengine.constant;

public class Constants {

    private Constants() {
    }

    public static final String ERROR_CREATING_DATA_SOURCE = "Error creating datasourcecatalog entry: ";
    public static final String ERROR_RETRIEVING_DATA_SOURCE_ENTRIES = "An error occurred while retrieving data source catalog entries";
    public static final String API_HEADER_ONESOURCE_JOB_EXECUTION_SUCCESS = "Successful creation of onsource data sync job";
    public static final String API_HEADER_NO_CONTENT = "No Content";
    public static final String API_HEADER_BAD_REQUEST = "Error processing the argument 'job_id'. No property found ";
    public static final String API_HEADER_SERVER_ERROR = "Internal Server Error";
    public static final String API_HEADER_CONTENT_TEXT = "text/plain";
    public static final String API_HEADER_CONTENT_JSON = "application/json";
    public static final String ENUM_VALIDATION_ERROR = "One or more enum validation errors occurred.";
    public static final String COMPLETED = "COMPLETED";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String FAILED = "FAILED";
    public static final String CSV_ERROR = "Error creating csv filename";
    public static final String NO_DATA = "No data found for the given ids";

    public static final String DATASYNC_JOB_SUCCESSFUL_FETCH = "DataSync Job retrieved successfully";
    public static final String API_UNAUTHORIZED = "Unauthorized";
    public static final String DATASYNC_JOB_BAD_REQUEST = "INVALID_REQUEST";
    public static final String DATASYNC_JOB_NOT_FOUND = "DataSync Job not found";
    public static final String JOB_EXECUTION_LOG_NOT_FOUND = "Job execution log not found";
    public static final String METAOBJECT_RELATION_NOT_FOUND = "MetaObjectRelationDTO not found";

    public static final String INVALID_AWS_S3_FILENAME = "Invalid AWS S3 filename format";
    public static final String ERROR_PROCESSING_FILE = "Error processing file";
    public static final String ERROR_DELETING_FILE = "Error while deleting the local file";

    public static final String MESSAGE = "message";
    public static final String CODE = "code";
    public static final String ERROR = "error";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String CONFLICT = "CONFLICT";
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String CONNECTION_URL = "jdbc:postgresql://localhost:5432/postgres";
    public static final String CONNECTION_USER = "postgresql";
    public static final String CONNECTION_PASSWORD = "postgresql";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String DATASYNC_AWS_FILE_UPLOAD_SUCCESS = "File uploaded successfully to S3 bucket";
    public static final String ERROR_PARSING_CSV = "An error occurred while parsing csv file";
    public static final String INSERT_LIST_KEY = "insertList";
    public static final String UPDATE_LIST_KEY = "updateList";
    public static final Integer BATCH_SIZE = 200;
    public static final String SERVICE_NAME = "dataconnect_executionengine";
    public static final String TARGET_RABBITMQ_CONNECTION_NAME = "targetRabbitMQConnection";
    public static final String SOURCE_RABBITMQ_CONNECTION_NAME = "sourceRabbitMQConnection";

    public static final String LOG_TYPE_ACTIVITY = "Activity";
    public static final String LOG_TYPE_ACTIVITY_FAILURE = "Activity Failure";
    public static final String LOG_CORRELATION_ID = "1";
    public static final Integer TENANT_COUNT = 1;
    public static final String LOG_COMPONENT_DATA_SYNC_JOB = "DataSyncJob";
    public static final String LOG_COMPONENT_TASK_EXECUTION_SERVICE = "TaskExecutionService";
    public static final String LOG_COMPONENT_DATABASE_ADAPTOR_SERVICE = "DatabaseAdaptorService";
    public static final String LOG_COMPONENT_JOB_LISTENER = "JobListener";
    public static final String LOG_COMPONENT_JOB_EXECUTION_ENGINE = "JobExecutionEngine";
    public static final String LOG_COMPONENT_DATASTREAM_LISTENER = "DataStreamListener";
    public static final String LOG_COMPONENT_DATASTREAM_ADAPTOR_SERVICE = "DataStreamAdaptorService";
    public static final String LOG_COMPONENT_FILE_ADAPTOR_SERVICE = "FileAdaptorService";
    public static final String LOG_COMPONENT_AWS_S3_SERVICE = "AwsS3Services";
    public static final Integer API_CALL_TIMEOUT = 10;
    public static final Integer API_CALL_ATTEMPT_TIMEOUT = 2;

    public static final String TRANSIT_HUB_RENEWAL_PERIOD = "30";
    public static final String LOOKUP = "LOOKUP";
    public static final String REFERENCE = "REFERENCE";
    public static final String PARENT = "PARENT";
    public static final String CHILD = "CHILD";
    public static final String ACCEPTED_AT = "ACCEPTED_AT";
    public static final String EXEC_TYPE = "EXEC_TYPE";

    public static final String INSERT_DATA_SET="InsertDataSet";
    public static final String UPDATE_DATA_SET="UpdateDataSet";
    public static final String DELETE_DATA_SET="DeleteDataSet";
    public static final String DEFAULT_TRANSITHUB_USERNAME="DataConnectUser";


}


