# ![Hospital Social App]
<p width="700" height="300" align="center" >
  <img src="https://dz2cdn1.dzone.com/storage/temp/13881958-1598464861104.png">
</p>



> ### Hospital social.

This is project for hospital center.Peopple can registartrion and after this they are have account. In this account you have your complaint, diagnosis, treatment.Also if you want ask question, you can send chat message to doctor in application.


 
Backend
--------

```sh
git clone https://github.com/drnserhio/hospitalmanageAnnotation.git
```


Frontend
--------

```sh
git clone https://github.com/drnserhio/hospitalManageFrontend.git
```

Full
--------

```sh
git clone https://github.com/drnserhio/hospitalManage.git
```

# Diagram

In this diagram you can see diagram entity user and relationship with one to many to (Analyze, Treatment, Video)

<p align="center">
  <img src="https://github.com/drnserhio/hospitalManage/blob/master/img-readme/diagram.png">
</p>

# How it works

The application uses Spring Boot (Web, Mybatis).

* Use the idea of Domain Driven Design to separate the business term and infrastructure term.
* Use MyBatis to implement the [Data Mapper](https://martinfowler.com/eaaCatalog/dataMapper.html) pattern for persistence.
* Use [CQRS](https://martinfowler.com/bliki/CQRS.html) pattern to separate the read model and write model.

And the code is organized as this:

1. `api` is the web layer implemented by Spring MVC
2. `core` is the business model including entities and services
3. `application` is the high-level services for querying the data transfer objects
4. `infrastructure`  contains all the implementation classes as the technique details

# Security

Integration with Spring Security and add other filter for jwt token process.

The secret key is stored in `application.properties`.

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
