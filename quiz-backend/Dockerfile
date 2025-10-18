FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# skip tests to speed up container build; CI should run tests separately
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-focal
WORKDIR /app
# copy the built jar (use wildcard to tolerate version changes)
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
