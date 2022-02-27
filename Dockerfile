FROM openjdk:17
COPY ./target/hospital-manage.jar hospital-manage.jar
CMD ["java", "-jar", "hospital-manage.jar"]

