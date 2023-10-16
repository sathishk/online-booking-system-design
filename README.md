# Book My Ticket
Sample System Design for Online Movie Booking

## Components

### API

```sh
./mvnw spring-boot:run
```
- JDK 21
- Spring Boot 3.2.0-M3
    - Spring Events
    - Spring JDBC
    - Spring Cache (Caffine)
    - Spring Validation (Service Level)
    - Spring Observability (TODO)
- Spring Security with JWT.
    - Convert Cookie to JWT for Social Login
    - Registration Check with Separate OTP
    - Token Obfuscation
- Flyway for Database Migrations
- Postgres/H2
- Elastic Search (Zero Downtime Reindexing)
- Open API 3
- Checkstyle with Exception for Doc as Controllers use  Open API
- Coverage With Jacoco with Package Control over Ratio
- ArchUnit for Layering and Best Practice Guarantee
- Owasp Security Plugin
- failOnWarning true to avoid deprecations
- Test Container for Integration Tests
- Modular Monolith (Zip Layout)
- Hexagonal (TODO)

### API Code Gen

```sh
./mvnw compile exec:java -Dexec.args="<<NAME_OF_ENTITY>>"
```
- String Templates
- Maven Exec Plugin

### Public App

```sh
nvm i
npm start
```
- JAM Stack
- Design System
- Hugo - Static Site Generator
- Switchable Login
- NPM Integration
- ItemJS - Client Side Search
- Headless CMS (TODO)

### Stakeholder App

```sh
nvm i
npm start
```

- ReactJS
- Formik