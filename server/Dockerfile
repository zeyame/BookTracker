# Use Eclipse Temurin JDK 21 as the base image
FROM eclipse-temurin:21

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the local machine to the container
COPY target/booktracker-0.0.1-SNAPSHOT.jar /app/booktracker.jar

# Expose port 8080
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/booktracker.jar"]
