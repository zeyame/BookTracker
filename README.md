# Shelf Quest

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Running the Project](#running-the-project)
  - [Option 1: Using Docker](#option-1-using-docker)
  - [Option 2: Running Applications Separately](#option-2-running-applications-separately)
- [Conclusion](#conclusion)

## Overview

 **Shelf Quest** is a user-friendly application designed to help book enthusiasts explore and manage their reading habits. With a vast selection of genres, users can search for their favorite books, add them to various reading lists, and track their reading progress. The app also features user account functionality, allowing users to create accounts and sign in to view their personalized reading data.

## Features

- **Explore and Search for Books**: Access a wide range of genres to find your next read.
- **Reading Lists**: Add books to different reading lists for better organization.
- **User Accounts**: Create an account and sign in to manage your reading preferences and lists.

## Tech Stack

This project is built using the following technologies:

- **Frontend**: React with TypeScript
- **Backend**: Spring Framework
- **Database**: PostgreSQL
- **Containerization**: Docker and Docker-Compose

## Running the Project

There are two ways to run the Book Tracker App:

### Option 1: Using Docker

**Prerequisites**: 
- Ensure you have Docker Engine installed on your system. If you don't have it installed, please visit the [official Docker website](https://docs.docker.com/get-docker/) and follow the installation instructions for your operating system.

1. **Set Up the Environment File**:
   Create a `.env` file in the root directory of the project with the following configurations:

   ```env
   # Database Configuration
   DB_NAME=book_tracker_db
   DB_USERNAME=postgres
   DB_PASSWORD=postgres

   # API Configuration
   GOOGLE_KEY=AIzaSyD7tk0i-j5aVlJtsyTuZeGI5Y--C-9AWJE
   TASTEDIVE_KEY=1033070-BookTrac-59A622F3

   # OTP Mail Configuration
   MAIL_USERNAME=shelfquest1@gmail.com
   MAIL_PASSWORD=efps yddz plul cvwh

   # JWT Configuration
   JWT_SECRET_KEY=870215c700072d0d78b8aad6cdc154d38b5100f1738c22941ea8ff57199dfe5b
   SECURITY_JWT_EXPIRATION_TIME=1800000
   ```

   You can use any values for the database configuration. The remaining values in the file MUST remain the same.

2. **Build and Run**: 
   Ensure you are in the project's root directory and execute the following commands in your terminal:
   ```bash
   cd server
   mvn clean package -DskipTests
   docker compose up --build -d
   ```

3. Access the Application: Visit http://localhost:3000 in your web browser to demo the app.


### Option 2: Running Applications Separately

#### Step 1: Install and Set Up PostgreSQL
1. Install PostgreSQL: Ensure you have PostgreSQL version 16.4 installed on your system.
2. Set Up the Database: Execute the SQL found in the `/schema` folder to set up the initial database schema.

#### Step 2: Configure Application Properties
1. Edit the Application Properties file: Open the `application.properties` file located in the `server/src/main/resources` directory and include the following data:

   ```properties
   spring.application.name=booktracker
   spring.main.web-application-type=servlet

   logging.level.org.springframework.web=DEBUG
   logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG

   spring.datasource.driver-class-name=org.postgresql.Driver
   spring.datasource.url=jdbc:postgresql://localhost:5432/YOUR_DB_NAME
   spring.datasource.username=YOUR_DB_USERNAME
   spring.datasource.password=YOUR_DB_PASSWORD

   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.hibernate.ddl-auto=update

   google.books.api-key=AIzaSyD7tk0i-j5aVlJtsyTuZeGI5Y--C-9AWJE
   tastedive.api-key=1033070-BookTrac-59A622F3

   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=shelfquest1@gmail.com
   spring.mail.password=efps yddz plul cvwh
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true

   security.jwt.secret-key=870215c700072d0d78b8aad6cdc154d38b5100f1738c22941ea8ff57199dfe5b
   security.jwt.expiration-time=1800000
   ```

   Replace the placeholder values with your own database configuration details.

#### Step 3: Build and Run the Server
1. Navigate to the Server Directory:
   ```bash
   cd server
   ```
2. Build the Server Project:
   ```bash
   mvn clean package
   ```
3. Run the JAR File:
   ```bash
   java -jar target/booktracker-0.0.1-SNAPSHOT.jar
   ```
   Or, simply click the run button in your preferred IDE.

#### Step 4: Run the Client App
1. Install Node.js: Ensure you have Node.js version 20.14.0 installed on your system.
2. Navigate to the Client Directory:
   ```bash
   cd client
   ```
3. Install Dependencies and Start the App:
   ```bash
   npm install
   npm start
   ```
4. Access the Application: Visit http://localhost:3000 in your web browser to demo the app.

## Conclusion ##
    This project was built to experiment with different technologies and bring together various components of modern web development. From React and TypeScript on the frontend to Spring Framework on the backend, and incorporating Docker for easy deployment, it provided an excellent opportunity to explore and integrate these powerful tools.