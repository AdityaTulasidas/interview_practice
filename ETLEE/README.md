# DataConnect execution engine

# Overview
This repository contains the code for the DataConnect execution engine.
The execution engine is responsible for executing data integration jobs and managing the execution flow.

# Prerequisites
- Java 17
- Maven 3.6 or higher
- PostgresSQL
- RabbitMQ
- OpenAPI
- Swagger
- Docker
- Docker Compose
- Postman
- PGAdmin
- IntelliJ IDEA
- Git
- Lombok Plugin for IntelliJ IDEA

# Getting Started

1. Clone the repository : https://github.com/tr/a208316_dataconnect_executionengine.git
2. Switch to the develop branch
3. Open the project in IntelliJ IDEA
4. Install Lombok Plugin in IntelliJ IDEA
5. Build the project using Maven
6. Run the project using IntelliJ IDEA or with the following command : shell mvn spring-boot:run
7. Access the Swagger UI at [http://localhost:8080/swagger-ui/index.html]
8. Use Postman/Swagger to test the APIs

# Environment Variables details
| Sno | Attribute                                           | Description             | Environment Variable                     | Example Value                                                     |
|-----|-----------------------------------------------------|-------------------------|------------------------------------------|-------------------------------------------------------------------|
| 1   | spring.rabbitmq.connections.source.host             | AMER RabbitMQ Host      | AMER_RABBITMQ_HOST                       | b-16120d98-9acb-4c37-bbb1-865333d843c8.mq.us-east-1.amazonaws.com |
| 2   | spring.rabbitmq.connections.source.port             | AMER RabbitMQ Port      | AMER_RABBITMQ_PORT                       | 5671                                                              |
| 3   | spring.rabbitmq.connections.source.username         | AMER RabbitMQ Username  | AMER_RABBITMQ_USERNAME                   | data-connect-user                                                 |
| 4   | spring.rabbitmq.connections.source.password         | AMER RabbitMQ Password  | AMER_RABBITMQ_PASSWORD                   | <zfMv<tr9vwGgBsd                                                  |
| 5   | spring.rabbitmq.connections.target.host             | EMEA RabbitMQ Host      | EMEA_RABBITMQ_HOST                       | b-6e54357a-853e-40e9-ac13-870c6c45ef89.mq.eu-west-2.on.aws        |
| 6   | spring.rabbitmq.connections.target.port             | EMEA RabbitMQ Port      | EMEA_RABBITMQ_PORT                       | 5671                                                              |
| 7   | spring.rabbitmq.connections.target.username         | EMEA RabbitMQ Username  | EMEA_RABBITMQ_USERNAME                   | data-connect-user                                                 |
| 8   | spring.rabbitmq.connections.target.password         | EMEA RabbitMQ Password  | EMEA_RABBITMQ_PASSWORD                   | hba6lW3TQXbgj3BjJAJ7uSLQOKhab3jN                                  |
| 9   | spring.rabbitmq.connections.source.queue.name       | Data Sync Queue         | DATA_SYNC_QUEUE                          | DATA_SYNC_QUEUE                                                   |
| 10  | spring.rabbitmq.connections.source.routing.key.name | Data Sync Routing Key   | DATA_SYNC                                | DATA_SYNC                                                         |
| 11  | spring.rabbitmq.connections.source.exchange.name    | Data Sync Exchange      | DATA_SYNC_EXCHANGE                       | DATA_SYNC_EXCHANGE                                                |
| 12  | spring.rabbitmq.connections.source.queue.name       | Data Task AMER Queue    | DATA_TASK_AMER                           | DATA_TASK_AMER                                                    |
| 13  | spring.rabbitmq.connections.source.routing.key.name | AMER Region Key         | AMER                                     | AMER                                                              |
| 14  | spring.rabbitmq.connections.source.exchange.name    | Data Task Exchange      | DATA_TASK_EXCHANGE                       | DATA_TASK_EXCHANGE                                                |
| 15  | spring.rabbitmq.listener.simple.acknowledge-mode    | Manual Acknowledge Mode | MANUAL                                   | MANUAL                                                            |
| 16  | spring.rabbitmq.connections.target.queue.name       | Data Task EMEA Queue    | DATA_TASK_EMEA                           | DATA_TASK_EMEA                                                    |
| 17  | spring.rabbitmq.connections.target.routing.key.name | EMEA Region Key         | EMEA                                     | EMEA                                                              |
| 18  | spring.rabbitmq.connections.target.host             | APAC RabbitMQ Host      | APAC_RABBITMQ_HOST                       | APAC_RABBITMQ_HOST                                                |
| 19  | spring.rabbitmq.connections.target.port             | APAC RabbitMQ Port      | APAC_RABBITMQ_PORT                       | 5671                                                              |
| 20  | spring.rabbitmq.connections.target.username         | APAC RabbitMQ Username  | APAC_RABBITMQ_USERNAME                   | APAC_RABBITMQ_USERNAME                                            |
| 21  | spring.rabbitmq.connections.target.password         | APAC RabbitMQ Password  | APAC_RABBITMQ_PASSWORD                   | APAC_RABBITMQ_PASSWORD                                            |
| 22  | spring.rabbitmq.connections.target.queue.name       | Data Task APAC Queue    | DATA_TASK_APAC                           | DATA_TASK_APAC                                                    |
| 23  | spring.rabbitmq.connections.target.routing.key.name | APAC Region Key         | APAC                                     | APAC                                                              |
| 24  | aws.s3.source-bucket-name                           | AMER S3 Bucket Name     | AMER_BUCKET_NAME                         | a209259-dev-data-connect-file-channel-use1                        |
| 25  | aws.s3.target-region.bucket-name                    | EMEA S3 Bucket Name     | EMEA_BUCKET_NAME                         | a209259-dev-data-connect-file-channel-euw2                        |
| 26  | aws.s3.target-region.bucket-name                    | APAC S3 Bucket Name     | APAC_BUCKET_NAME                         | APAC_BUCKET_NAME                                                  |
| 27  | spring.server.ui-url                                | AMER UI URL             | AMER_BASE_URL                         | http://localhost:4200                                             |

# Note - Queue, Exchange and Routing Key names

The example names for queues, exchanges, and routing keys are provided in the environment variables table are the actual names should be used in the services.

# Note - DB name

DB name is passed as a part of the connection string. So there is no attribute for DB name in the environment variables.
example connection string: jdbc:postgresql://localhost:5432/postgres

# Note

This repo does not have any database scripts.

# Health Check endpoint
The health check endpoint returns the status of the application and its dependencies.

The health check endpoint is available at [https://localhost:8080/data-connect/execution-engine/actuator/health]

**Note**: Please note that in the above URL, localhost is the hostname of the server where the application is running.
This should be replaced with the actual hostname or IP address of the server if you are accessing it remotely.

The urls mentioned above are for local development. The urls for the dev and prod environments will be different.

ACKNOWLEDGEMENT_MODE=MANUAL;AMER_BASE_URL=http://internal-a209259-dev-data26ac2fd16bc924b4-933279134.us-east-1.elb.amazonaws.com;AMER_BUCKET_NAME=a209259-dev-data-connect-file-channel-use1;AMER_RABBITMQ_HOST=b-c7bf694f-b550-4e9e-9ff3-162c9b1b148b.mq.us-east-1.on.aws;AMER_RABBITMQ_PASSWORD=g4Jp0RnYedCuAu1z2puHomloUlOdWKw9;AMER_RABBITMQ_PORT=5671;AMER_RABBITMQ_USERNAME=data-connect-user;APAC_BUCKET_NAME=APAC_BUCKET_NAME;APAC_RABBITMQ_HOST=APAC_RABBITMQ_HOST;APAC_RABBITMQ_PASSWORD=APAC_RABBITMQ_PASSWORD;APAC_RABBITMQ_PORT=5671;APAC_RABBITMQ_USERNAME=APAC_RABBITMQ_USERNAME;DATA_SYNC_EXCHANGE=DATA_SYNC_EXCHANGE;DATA_SYNC_QUEUE=DATA_SYNC_QUEUE;DATA_SYNC_ROUTING_KEY=DATA_SYNC;DATA_SYNC_ROUTING_KEY_AMER=AMER;DATA_SYNC_ROUTING_KEY_APAC=APAC;DATA_SYNC_ROUTING_KEY_EMEA=EMEA;DATA_TASK_EXCHANGE=DATA_TASK_EXCHANGE;DATA_TASK_QUEUE_AMER=DATA_TASK_AMER;DATA_TASK_QUEUE_APAC=DATA_TASK_APAC;DATA_TASK_QUEUE_EMEA=DATA_TASK_EMEA;DATACONNECT_EXECUTION_ENGINE_DB_CONN_STR=a209259-dev-data-connect-shared-db.c69sniltamfr.us-east-1.rds.amazonaws.com;DATACONNECT_EXECUTION_ENGINE_DB_PASSWORD=URvnNuWA3vbGUVnAsuYw19rz8wynAmfc;DATACONNECT_EXECUTION_ENGINE_DB_USER=dataconnect;EMEA_BUCKET_NAME=a209259-dev-data-connect-file-channel-euw2;EMEA_RABBITMQ_HOST=b-6e54357a-853e-40e9-ac13-870c6c45ef89.mq.eu-west-2.on.aws;EMEA_RABBITMQ_PASSWORD=hba6lW3TQXbgj3BjJAJ7uSLQOKhab3jN;EMEA_RABBITMQ_PORT=5671;EMEA_RABBITMQ_USERNAME=data-connect-user;EXECUTION_ENGINE_DB=dataconnectdb;MAX_CONCURRENT_CONSUMERS=10;port=9082;S3_AMER_REGION=us-east-1;S3_EMEA_REGION=eu-west-2;S3_INPUT_FOLDER=input;SPRING_PROFILES_ACTIVE=DEVAMER;CONCURRENT_CONSUMERS=5
