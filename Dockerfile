FROM adoptopenjdk/maven-openjdk11:latest AS build
COPY src /app/src
COPY pom.xml /app
WORKDIR /app
RUN mvn clean install

FROM openjdk:11-jre-slim
COPY --from=build /app/target/serialminder-0.0.1-SNAPSHOT.jar /app/application.jar
ENTRYPOINT ["java", "-jar", "/app/application.jar"]