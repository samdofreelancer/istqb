FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean install

FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=build /app/target/quiz-backend-0.0.1-SNAPSHOT.jar .
EXPOSE 8081
CMD ["java", "-jar", "quiz-backend-0.0.1-SNAPSHOT.jar"]
