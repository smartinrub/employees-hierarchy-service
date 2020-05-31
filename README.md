# Hierarchy Service

## Prerequisites

- Java 8+

## Running Hierarchy Service

```shell script
./gradlew build
./gradlew bootrun
```

## Running Tests

```shell script
./gradlew test
```

## API Usage

Example of a valid API request using cURL:

`curl -X POST  -H "Content-Type: text/plain" -d '{"Pete": "Nick", "Barbara": "Nick", "Nick": "Sophie", "Sophie": "Jonas"}' localhost:8080/employees`
