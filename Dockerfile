FROM maven:3.8.5-openjdk-17 as builder

# copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# build a release artifact
RUN mvn package -DskipTests

# Run the web service on container startup.
CMD ["java", "-jar", "/app/target/user-center-backend-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]