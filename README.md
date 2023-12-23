## Reactive Spring Webflux

A simple Reactive RESTful microservices application developed using Spring Webflux.

### Tech stack
- Spring Boot
- Mongo DB with Mongo Express admin tool
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

#### Prerequisites
- Mongo DB up and running locally (via Docker or local installation)

#### Steps
- **Step1:** Starting Mongo DB in Docker: go to `docker` folder and execute the code below.
    
    ```agsl
    docker-compose  -f common.yml -f mongo-service.yml up
    ```
  ![](https://i.imgur.com/d9NAkY2.png)
- **Step2:** Starting the microservices: Run all 3 microservice in any order in intelliJ.

  ![](https://i.imgur.com/XTuRgCy.png)

### Build and run application via Docker
This will allow to build and run all the microservices and Mondo DB in Docker.

#### Prerequisites
- Docker installation


#### Steps
- **Step1:** The project needs to be built first using the command mvn install -DskipTests via intelliJ or in commandline. It should be executed under the root project `reactive-spring-webflux` as shown below.

  ![](https://i.imgur.com/d8POEyR.png)
- **Step 2:** Execute the following docker command inside the `docker` directory in order to bring up the Spring project and the Kafka servers.
  ```
  docker-compose up
  ```
  ![](https://i.imgur.com/1z3KUO3.png)


### Consuming the microservices via endpoints
The cURL requests can be found in the `src/main/resources` folder of each project.

### Mongo express
Mongo Express can be used for accessing the data in the DB.
The defualt credentials are "admin:pass"
![](https://i.imgur.com/8yk8HAK.png)

### TDD Development
Each microservice has its own set of test cases located in `src/test` folder, encompassing both unit and integration tests.
The external services and the dependencies are mocked using `WireMock` and `Mockito` libraries.
