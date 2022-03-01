# Hospital Social App
<p  align="center" >
  <img width="900" height="500" src="https://github.com/drnserhio/hospitalManage/blob/master/img-readme/profile.png">
</p>



This is project for hospital center.People can registartrion and after this they are have account. In this account you have your complaint, diagnosis, treatment.Also if you want ask question, you can send chat message to doctor in application.


Clone
--------

```sh
git clone https://github.com/drnserhio/hospitalManage.git
```

# Diagram

In this diagram you can see diagram entity user and relationships with one to many to (Analyze, Treatment, Video)

<p align="center">
  <img width="400" height="400" src="https://github.com/drnserhio/hospitalManage/blob/master/img-readme/diagram.png">
</p>

# How it works

The application uses Spring Boot,Spring WebSocket, Java Mail, MySQL, Mongo, Angular.

1. `Spring boot` uses the idea of manipulation infromation about patient in hospital.
2. `Spring WebSocket` uses stomp connection in js ui.
3. `Java mail` uses google stmp and send message information for user.
4. `MySQL` uses manipulation entity User, Analyze, Treatmen, Video and relationships.
5. `Mongo` uses manipulation fast input, output chat messages.
6. `Angular` uses component manipulation data json for api backend.

# Security

Spring Security was override and add other filter for jwt token process.

The secret key is stored in `application.yml`.

# Database

It uses a ~~H2 in-memory database~~ sqlite database (for easy local test without losing test data after every restart), can be changed easily in the `application.properties` for any other database.

# Getting started

You'll need Java 8 installed.

    ./gradlew bootRun

To test that it works, open a browser tab at http://localhost:8080/tags .  
Alternatively, you can run

    curl http://localhost:8080/tags

# Try it out with [Docker](https://www.docker.com/)

You'll need Docker installed.
	
    ./gradlew bootBuildImage --imageName spring-boot-realworld-example-app
    docker run -p 8081:8080 spring-boot-realworld-example-app

# Try it out with a RealWorld frontend

The entry point address of the backend API is at http://localhost:8080, **not** http://localhost:8080/api as some of the frontend documentation suggests.

# Run test

The repository contains a lot of test cases to cover both api test and repository test.

    ./gradlew test

# Code format

Use spotless for code format.

    ./gradlew spotlessJavaApply

# Help

Please fork and PR to improve the project.
