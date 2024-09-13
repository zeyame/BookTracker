FROM openjdk:21-jdk-slim

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /server-app

# Copy the pom.xml
COPY ./pom.xml .

# install dependencies
RUN mvn dependency:go-offline

# Copy the rest of the source code and package the application
COPY . .

# build the project
RUN mvn clean package

EXPOSE 8080

CMD ["java", "-jar", "target/booktracker-0.0.1-SNAPSHOT.jar"]
