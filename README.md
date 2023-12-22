## Reactive Spring Webflux

A simple Reactive RESTful microservices application developed using Spring Webflux.

### Tech stack
- Spring Boot
- Mongo DB with Mongo Express admin panel
- Docker

### Application architecture
![](https://i.imgur.com/E6tbJIv.png)

### Microservices and features
- **movies-info-service**
  - CRUD movies
  - Stream API for getting the latest movies
- **movies-review-service**
  - CRUD reviews
  - Stream API for getting the latest reviews
- **movies-service**
  - Retrieve movies with reviews
  - Stream API for getting the latest movies

### Build and run application via IntelliJ

### Prerequisites
- Mongo DB up and running locally (via Docker or local installation)

### Steps
- Starting Mongo DB in Docker: go to `docker` folder and execute the code below.
    
    ```agsl
    docker-compose  -f common.yml -f mongo-service.yml up
    ```
  ![](https://i.imgur.com/d9NAkY2.png)
- Starting the microservices: Run all 3 microservice in any order in intelliJ.
    ![](https://i.imgur.com/XTuRgCy.png)

### Build and run application via Docker
To be updated

### Consuming the microservices via endpoints
The cURL requests can be found in the `src/main/resources` folder of each project.

### TDD Development
Each microservice has its own set of test cases located in `src/test` folder, encompassing both unit and integration tests.
The external services and the dependencies are mocked using `WireMock` and `Mockito` libraries.
