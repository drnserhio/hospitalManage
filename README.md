# Hospital Social App
<p  align="center" >
  <img width="900" height="500" src="https://github.com/drnserhio/hospitalManage/blob/master/img-readme/profile.png">
</p>



This is project for hospital center.People can registartrion and after this they are have account. In this account you have your diagnosis, treatments.Also if you want ask question, you can send chat message to doctor in application.


Clone
--------

```sh
git clone https://github.com/drnserhio/hospitalManage.git
```

# Diagram

In this diagram you can see diagram entity user and relationships with one to many to (Analyze, Treatment, Video)

<p align="center">
  <img width="400" height="500" src="https://github.com/drnserhio/hospitalManage/blob/master/img-readme/diagram.png">
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

🔑 Role: 
1. `Super Admin`
2. `Admin`
3. `Secretary`
4. `Doctor`
5. `User` 

When you start applicatiion you have 5 users.

1. `Super Admin` - login: ```shrick``` `password:` 5600
2. `Admin` - `login:` morthy `password:` 5600
3. `Secretary` - `login:` betty `password:` 5600
4. `Doctor` - `login:` summer `password:` 5600
5. `User`  - `login:` jerry `password:` 5600


# Database

- `H2`
Test uses a ~~H2 in-memory database~~. In test folder resources initial sql schema and `application-test.yml`

- `MySQL`
 Hibernate auto create table. In folder resources properties `application.yml`.

- `Mongo`
 Collections auto create. In folder resources properties `application.yml`.



# Getting started 

You'll need Java 17 installed. `Maven jar`

    deploy.sh

# Try it out with [Docker](https://www.docker.com/)

You'll need Docker installed. `Docker compose`
	
    deploy-docker.sh
    
To test that it works, open a browser tab at http://localhost:4200 .      

# Help

Please fork and PR to improve the project.
