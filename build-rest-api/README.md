# Build REST APIs

## Dependencies

- Spring Web
- H2 Database
- Spring Data JPA
- Spring Validation for REST API parameters validation
- Lombok
- [MapStruct](https://mapstruct.org/) for DTO bean mapping

## Features

- Build REST APIS
- Database access
- Parameter validation
- Bean mapping
- Global exception handling

## Using MapStruct in Spring Boot project with Lombok

Properties:

```xml
<properties>
    <java.version>17</java.version>
    <lombok.version>1.18.26</lombok.version>
    <org.mapstruct.version>1.5.5.Final</org.mapstruct.version>
</properties>
```

Import MapStruct dependency:

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>${org.mapstruct.version}</version>
</dependency>
```

Configure `maven-compiler-plugin` plugin:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.0</version>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>${org.mapstruct.version}</version>
            </path>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok-mapstruct-binding</artifactId>
                <version>0.2.0</version>
            </dependency>
        </annotationProcessorPaths>
        <compilerArgs>
            <compilerArg>
                -Amapstruct.defaultComponentModel=spring
            </compilerArg>
        </compilerArgs>
    </configuration>
</plugin>
```


References
- https://springframework.guru/using-mapstruct-with-project-lombok/
- https://github.com/mapstruct/mapstruct-examples/blob/main/mapstruct-lombok/pom.xml

## Test REST API

Use HTTPie for API testing.

### Add a customer

Request:
```bash
http post :8080/customers name="abc" address="xyz street"
```

Response:
```json
{
    "address": "xyz street",
    "id": 1,
    "name": "abc"
}
```

Add other customers:
```bash
http post :8080/customers name="icbc" address="finance street"
```

### Find all customers

Request:
```bash
http get :8080/customers
```

Response:
```json
[
  {
    "address": "xyz street",
    "id": 1,
    "name": "abc"
  },
  {
    "address": "finance street",
    "id": 2,
    "name": "icbc"
  }
]
```

### Update a customer

Request:
```bash
http put :8080/customers/1 name="abc" address="finance street"
```

Response:
```json
{
  "address": "finance street",
  "id": 1,
  "name": "abc"
}
```

### Find a customer

Request:
```bash
http get :8080/customers/1
```

Response:
```json
{
  "address": "finance street",
  "id": 1,
  "name": "abc"
}
```

### Find a not found customer

Request:
```bash
http get :8080/customers/99
```
Response:
```json
{
    "code": 404,
    "errors": [
        "Could not find customer: id=99"
    ],
    "message": "Could not find customer: id=99",
    "status": "NOT_FOUND"
}
```

### Find a customer with wrong id type

Request:
```bash
http get :8080/customers/abc
```
Response:
```json
{
  "code": 400,
  "errors": [
    "id should be of type java.lang.Long"
  ],
  "message": "The method argument is not the expected type",
  "status": "BAD_REQUEST"
}
```


### Add a customer with missing fields

Request:
```bash
http post :8080/customers name="boc"
```
Response:
```json
{
  "code": 400,
  "errors": [
    "address: Address is mandatory"
  ],
  "message": "The method argument is not valid",
  "status": "BAD_REQUEST"
}
```
