# DataIntegration Service
DataIntegration is a service that provides an interface for managing and executing data integration tasks.
It allows to create, update, and delete job configurations, as well as execute them on demand.


## Prerequisites
- Java 17
- Maven 3.6 or higher
- PostgreSQL
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

## Getting Started

1. Clone the repository : https://github.com/tr/a208316_dataconnect-dataintegration.git
2. Switch to the develop branch
3. Open the project in IntelliJ IDEA
4. Install Lombok Plugin in IntelliJ IDEA
5. Build the project using Maven
6. Run the project using IntelliJ IDEA or with the following command : shell mvn spring-boot:run
7. Access the Swagger UI at [http://localhost:8080/swagger-ui/index.html]
8. Use Postman/Swagger to test the APIs


## Environment Variables details

| Sno | Attribute                              | Description                | Environment Variable                     | Example Value         |
|-----|----------------------------------------|----------------------------|------------------------------------------|-----------------------|
| 1   | datasource.url                         | Server hostname            | DATACONNECT_DATA_INTEGRATION_DB_CONN_STR | localhost             |
| 2   | datasource.username                    | DB username                | DATACONNECT_DATA_INTEGRATION_DB_USER     | postgresql            |
| 3   | datasource.password                    | DB Password                | DATACONNECT_DATA_INTEGRATION_DB_PASSWORD | postgresql            |
| 4   | NA                                     | DB name                    | DATA_INTEGRATION_DB                      | postgres              |
| 6   | NA                                     | UI URL                     | AMER_BASE_URL                            | http://localhost:4200 |
| 7   | spring.rabbitmq.host                   | AMER RabbitMQ Host         | SOURCE_AMER_RABBITMQ_HOST                | localhost             |
| 8   | spring.rabbitmq.port                   | AMER RabbitMQ Port         | SOURCE_AMER_RABBITMQ_PORT                | 5671                  |
| 9   | spring.rabbitmq.username               | AMER Username              | SOURCE_AMER_RABBITMQ_USERNAME            | guest                 |
| 10  | spring.rabbitmq.password               | AMER Password              | SOURCE_AMER_RABBITMQ_PASSWORD            | guest                 |
| 11  | spring.rabbitmq.host                   | EMEA RabbitMQ Hos          | SOURCE_EMEA_RABBITMQ_HOST                | localhost             |
| 12  | spring.rabbitmq.port                   | EMEA RabbitMQ Port         | SOURCE_EMEA_RABBITMQ_PORT                | 5671                  |
| 13  | spring.rabbitmq.username               | EMEA Username              | SOURCE_EMEA_RABBITMQ_USERNAME            | guest                 |
| 14  | spring.rabbitmq.password               | EMEA Password              | SOURCE_EMEA_RABBITMQ_PASSWORD            | guest                 |
| 15  | spring.rabbitmq.queue.name             | Source queue of AMER       | SOURCE_AMER_QUEUE                        | source_queue          |
| 16  | spring.rabbitmq.queue.name             | Source queue of EMEA       | SOURCE_EMEA_QUEUE                        | source_queue          |
| 17  | spring.rabbitmq.queue.routing.key.name | Source Routing key in AMER | SOURCE_AMER_ROUTING_KEY                  | source_routing_key    |
| 18  | spring.rabbitmq.exchange.name          | Source Exchange in AMER    | SOURCE_AMER_EXCHANGE                     | source_exchange       |
| 19  | spring.rabbitmq.exchange.name          | Source Exchange in EMEA    | SOURCE_EMEA_EXCHANGE                     | source_exchange       |
| 20  | spring.rabbitmq.queue.routing.key.name | Source routing key in EMEA | SOURCE_EMEA_ROUTING_KEY                  | source_routing_key    |
| 21  | spring.profiles.active                 | spring profile             | SPRING_PROFILES_ACTIVE                   | AMER/EMEA/APAC        |

# Note on Queue, exchange and Routing Key Names

The queue, exchange, and routing key names are used to configure the RabbitMQ message broker for the DataIntegration service.
These are the names that the service will use to send and receive messages from RabbitMQ.

DB name is passed as a part of the connection string. So there is no attribute for DB name in the environment variables.
example connection string: jdbc:postgresql://localhost:5432/postgres


## Health Check endpoint
The health check endpoint returns the status of the application and its dependencies.

The health check endpoint is available at [http://localhost:8080/data-connect/data-integration/actuator/health]

**Note**: Please note that in the above URL, localhost is the hostname of the server where the application is running.
This should be replaced with the actual hostname or IP address of the server if you are accessing it remotely.


AMER_BASE_URL=http://internal-a209259-dev-data26ac2fd16bc924b4-933279134.us-east-1.elb.amazonaws.com;CONCURRENT_CONSUMERS=2;DATA_INTEGRATION_DB=dataconnectdb;DATACONNECT_DATA_INTEGRATION_DB_CONN_STR=a209259-dev-data-connect-shared-db.c69sniltamfr.us-east-1.rds.amazonaws.com;DATACONNECT_DATA_INTEGRATION_DB_PASSWORD=URvnNuWA3vbGUVnAsuYw19rz8wynAmfc;DATACONNECT_DATA_INTEGRATION_DB_USER=dataconnect;MAX_CONCURRENT_CONSUMERS=10;RABBITMQ_PORT=5671;RABBITMQ_USERNAME=data-connect-user;RECEIVE_RABBITMQ_HOST=b-045c627e-b875-49cd-be10-f66b4b0b7872.mq.us-east-1.amazonaws.com;SOURCE_AMER_RABBITMQ_HOST=b-c7bf694f-b550-4e9e-9ff3-162c9b1b148b.mq.us-east-1.on.aws;SOURCE_AMER_RABBITMQ_PASSWORD=g4Jp0RnYedCuAu1z2puHomloUlOdWKw9;SOURCE_AMER_RABBITMQ_PORT=5671;SOURCE_AMER_RABBITMQ_USERNAME=data-connect-user;SPRING_PROFILES_ACTIVE=local;RABBITMQ_HOST=b-16120d98-9acb-4c37-bbb1-865333d843c8.mq.us-east-1.amazonaws.com;RABBITMQ_PASSWORD=<zfMv<tr9vwGgBsd