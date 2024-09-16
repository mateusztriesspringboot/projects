# Projects Service

Projects is a simple application that stores users and their projects.
It uses:
- Spring Boot for java application
- MySQL as datastore
- JDBI3 for database access
- OpenAPI for API contract
- Liquibase for schema changes

## Features

- Create user
- Get uer
- Delete user
- Add new project
- List projects
- Secured with Basic Auth and TLS!

Specification of the api can be found in `spec/api.yaml`

## Requirements

- Java 17
- Maven
- Unix OS (to run bash generation script)
- Docker and Docker Compose

## How to run

1. `mvn clean install`
2. `./createSelfSignedCertAndPasswords.sh`
3. `docker compose up`

Docker compose will start a separate MySQL container as well as the Projects service
once the MySQL is ready to accept connections. By default, projects api will be accessible on localhost:8080.
E.g.:
`https://localhost:8080/users/1`


## Administration port

Metrics and health status are available on the dedicated admin port (default 8081) `https://localhost:8081/admin/`

Admin port should never be accessible to the outside world. In current configuration, admin port should only be used
by internal tools over the internal network.

Example to get jvm used memory metric - `https://localhost:8081/admin/metrics/jvm.memory.used`
Example to get app health status - `https://localhost:8081/admin/health`

