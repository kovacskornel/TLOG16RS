# TLOG16RS - Time Logger Application

A RESTful web service for time tracking and task management built with Java and Dropwizard framework.

## Overview

TLOG16RS is a time logging application that allows users to track their work hours, manage tasks, and organize workdays by month. It provides JWT-based authentication and a comprehensive REST API for time tracking operations.

## Features

- **User Management**: User registration and JWT-based authentication
- **Work Month Management**: Create and manage monthly work records
- **Work Day Tracking**: Track daily work including weekends
- **Task Management**: Start, finish, modify, and delete tasks
- **Time Tracking**: Accurate time logging with validation for task durations
- **Database Support**: MariaDB database integration with Liquibase migrations
- **Dockerized**: Ready for containerized deployment

## Technology Stack

- **Java 8**
- **Dropwizard 0.8.2**: RESTful web service framework
- **Avaje Ebean ORM 7.1.1**: Object-relational mapping
- **MariaDB**: Database
- **Liquibase**: Database migrations
- **JWT (JSON Web Tokens)**: Authentication
- **Lombok**: Reduce boilerplate code
- **Docker**: Containerization
- **Gradle**: Build automation

## Prerequisites

- Java 8 or higher
- Gradle (tested with Gradle 4.x and later)
- MariaDB database
- Docker (optional, for containerized deployment)

## Getting Started

### Database Setup

1. Start a MariaDB instance (or use Docker Compose)
2. Create the database schema using the provided SQL scripts:
   ```bash
   mysql -u root -p < timelogger-create-all.sql
   ```

### Configuration

Update the configuration file `src/dist/config.yml` with your database settings:

```yaml
dbDriver: org.mariadb.jdbc.Driver
dbUrl: jdbc:mariadb://127.0.0.1:9005/timelogger
dbUsername: your_database_username
dbPassword: your_database_password
dbName: timelogger
```

**Note**: Never commit sensitive credentials to version control. Use environment variables or secure configuration management in production.

### Building the Application

```bash
gradle build
```

### Running the Application

```bash
gradle run
```

The application will start on the default Dropwizard port (8080).

### Using Docker

Build and run using Docker Compose:

```bash
docker-compose up
```

The application will be available at `http://localhost:9080`

## API Endpoints

### Authentication

- `POST /timelogger/register` - Register a new user
- `POST /timelogger/authenticate` - Authenticate and receive JWT token
- `POST /timelogger/refresh` - Refresh JWT token

### Work Months

- `GET /timelogger/workmonths` - Get all work months
- `POST /timelogger/workmonths` - Create a new work month
- `GET /timelogger/workmonths/{year}/{month}` - Get specific month data
- `POST /timelogger/workmonths/deleteall` - Delete all work months

### Work Days

- `POST /timelogger/workmonths/workdays` - Create a new work day
- `POST /timelogger/workmonths/workdays/weekend` - Create a weekend work day
- `GET /timelogger/workmonths/{year}/{month}/{day}` - Get specific day data

### Tasks

- `POST /timelogger/workmonths/workdays/tasks/start` - Start a new task
- `PUT /timelogger/workmonths/workdays/tasks/finish` - Finish a task
- `PUT /timelogger/workmonths/workdays/tasks/modify` - Modify a task
- `PUT /timelogger/workmonths/workdays/tasks/delete` - Delete a task

## Testing

Run the test suite:

```bash
gradle test
```

## Project Structure

```
TLOG16RS/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/kovacskornel/tlog16rs/
│   │   │       ├── core/           # Core domain exceptions
│   │   │       ├── resources/      # REST resources and entities
│   │   │       ├── TLOG16RSApplication.java
│   │   │       ├── TLOG16RSConfiguration.java
│   │   │       └── CreateDatabase.java
│   │   └── resources/
│   │       └── migrations.xml      # Liquibase migrations
│   ├── dist/
│   │   └── config.yml             # Application configuration
│   └── test/
│       └── java/                  # Unit tests
├── database/                      # Database scripts
├── build.gradle                   # Gradle build configuration
├── docker-compose.yml            # Docker Compose configuration
└── README.md                     # This file
```

## Database Cleanup

To drop all database tables:

```bash
mysql -u root -p < timelogger-drop-all.sql
```

## License

This project is part of the TLOG16RS training exercise.

## Author

Kovács Kornél
