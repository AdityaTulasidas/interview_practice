# DataConnect-MetaDataRegistry
Data Connect MetaData Registry is a service that provides a set of APIs to manage metadata for Data Connect services.
It allows you to create, update metadata for Data Connect services.

# Local Development Setup
## Prerequisites
- Java 17
- Maven 3.6.3
- Docker
- Docker Compose
- Postman
- PGAdmin
- IntelliJ IDEA
- Git
- Lombok Plugin for IntelliJ IDEA

## Setup
1. Clone the repository : https://github.com/tr/a208316_dataconnect-metadataregistry.git
2. Switch to the develop branch
3. Open the project in IntelliJ IDEA
4. Install Lombok Plugin in IntelliJ IDEA
5. Build the project using Maven
6. Run the project using IntelliJ IDEA the following command :
   ```shell mvn spring-boot:run```
7. Access the Swagger UI at [http://localhost:8080/metadataregistry/swagger-ui/index.html]
8. Use Postman/Swagger to test the APIs

## Environment Variables details
| Sno | Attribute           | Description     | Environment Variable                      | Example Value         |
|-----|---------------------|-----------------|-------------------------------------------|-----------------------|
| 1   | datasource.url      | Server hostname | DATACONNECT_METADATA_REGISTRY_DB_CONN_STR | localhost             |
| 2   | datasource.username | DB username     | DATACONNECT_METADATA_REGISTRY_DB_USER     | postgresql            |
| 3   | datasource.password | DB Password     | DATACONNECT_METADATA_REGISTRY_DB_PASSWORD | postgresql            |
| 4   | NA                  | DB name         | METADATA_REGISTRY_DB                      | postgres              |
| 5   | NA                  | UI URL          | CORS_ALLOWED_ORIGINS                             | http://localhost:4200 |

Note: DB name is passed as a part of the connection string. So there is no attribute for DB name in the environment variables.
example connection string: jdbc:postgresql://localhost:5432/postgres

## Health Check endpoint
The health check endpoint returns the status of the application and its dependencies.

The health check endpoint is available at [http://localhost:8080/metadataregistry/actuator/health]

**Note**: Please note that in the above URL, localhost is the hostname of the server where the application is running.
This should be replaced with the actual hostname or IP address of the server if you are accessing it remotely.

The urls mentioned above are for local development. The urls for the dev and prod environments will be different.



