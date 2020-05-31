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

- Create and get employees hierarchy

`curl -X POST  -H "Content-Type: text/plain" -d '{"Pete": "Nick", "Barbara": "Nick", "Nick": "Sophie", "Sophie": "Jonas"}' localhost:8080/employees`

- Get supervisor name and supervisor's supervisor name

`curl -X GET localhost:8080/employees/{name}/supervisor`

e.g. 

`curl -X GET localhost:8080/employees/Nick/supervisor`
