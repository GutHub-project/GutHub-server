# build stage
FROM eclipse-temurin:25 AS build
WORKDIR /app
COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean bootJar -x test


# runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","/app/app.jar"]
