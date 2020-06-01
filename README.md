# Hierarchy Service

## Prerequisites

- Java 8+

## Running Hierarchy Service

```
./gradlew build
./gradlew bootrun
```

## Running Tests

```
./gradlew test
```

## API Usage

- Get authentication token. User is preloaded in DB with credentials: 
    - username: admin
    - password: secret
    
```
curl -X POST -H "Content-Type: application/json" -d '{"username": "admin", "password": "secret"}' localhost:8080/authenticate
```
    
- Response example:
    
```
YMKXuwGFC_sFZB-sreygU9zMSYjte_sH
```

- Create and get employees hierarchy Example

```
curl -X POST -H "Content-Type: application/json" -H "Authorization: YMKXuwGFC_sFZB-sreygU9zMSYjte_sH" -d '{"Pete": "Nick", "Barbara": "Nick", "Nick": "Sophie", "Sophie": "Jonas"}' localhost:8080/employees
```

- Response example:
    
```json
{
  "Jonas" : {
    "Sophie" : {
      "Nick" : {
        "Pete" : { },
        "Barbara" : { }
      }
    }
  }
}
```

- Get supervisor name and supervisor's supervisor name

```
curl -X GET -H "Authorization: <TOKEN>" localhost:8080/employees/{name}/supervisor
```

- Request example:

```
curl -X GET -H "Authorization: YMKXuwGFC_sFZB-sreygU9zMSYjte_sH" localhost:8080/employees/Nick/supervisor
```

- Response example:
    
```json
{
  "name":"Sophie",
  "supervisor":"Jonas"
}
```
