# Spring Boot Quick Start

## A simple REST API

### Run the application

```bash
mvn clean install
mvn spring-boot:run
```

### Test the REST API

Greeting without parameters:

Request:
```bash
http :8080/greeting
```

Response:
```json
{
    "content": "Hello, World!",
    "id": 1
}
```

Greeting with parameters:

Request:
```bash
http :8080/greeting?name=William
```

Response:
```json
{
  "content": "Hello, William!",
  "id": 2
}
```
