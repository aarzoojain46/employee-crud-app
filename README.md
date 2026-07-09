# Employee Management CRUD API

A REST API for managing employee records, built with Spring Boot. Supports full CRUD operations (Create, Read, Update, Delete), request validation, and standardized error handling.

## Project Overview

This application exposes a set of REST endpoints to manage `Employee` records (name, email, salary). It was built as a hands-on learning project covering:

- Layered architecture (Controller → Service → Repository)
- DTO-based request/response separation from the entity
- Input validation using Jakarta Bean Validation
- Centralized exception handling with standardized error responses
- Unit testing with JUnit 5 and Mockito across controller and service layers
- API documentation via Swagger/OpenAPI

## Technologies Used

- **Java 17**
- **Spring Boot 3.5.x**
  - Spring Web
  - Spring Data JPA
  - Spring Validation
- **H2 Database** (in-memory, for development/testing)
- **Maven** (build tool)
- **JUnit 5 + Mockito** (unit testing)
- **springdoc-openapi** (Swagger UI)

## Steps to Run the Application

### Prerequisites
- Java 17 or later installed
- Maven (or use the included `mvnw` wrapper — no separate install needed)

### Run locally

```bash
# Clone the repository
git clone https://github.com/your-username/employee-crud-app.git
cd employee-crud-app

# Run the application
./mvnw spring-boot:run
```

The application starts on **http://localhost:8080**.

### Run tests

```bash
./mvnw test
```

### Access Swagger UI

Once running, view and try out the API interactively at:
```
http://localhost:8080/swagger-ui/index.html
```

### Access H2 Console (dev database)

```
http://localhost:8080/h2-console
```
Default JDBC URL: `jdbc:h2:mem:employeedb` (check `application.properties` for exact config)

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|--------------|
| POST | `/employees` | Create a new employee |
| GET | `/employees` | Get all employees |
| GET | `/employees/{id}` | Get an employee by ID |
| PUT | `/employees/{id}` | Update an existing employee |
| DELETE | `/employees/{id}` | Delete an employee by ID |

## Sample Requests/Responses

### Create Employee
**Request:** `POST /employees`
```json
{
  "name": "Aarzoo Sharma",
  "email": "aarzoo@example.com",
  "salary": 50000.0
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "Aarzoo Sharma",
  "email": "aarzoo@example.com",
  "salary": 50000.0
}
```

### Get Employee by ID
**Request:** `GET /employees/1`

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Aarzoo Sharma",
  "email": "aarzoo@example.com",
  "salary": 50000.0
}
```

### Get All Employees
**Request:** `GET /employees`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Aarzoo Sharma",
    "email": "aarzoo@example.com",
    "salary": 50000.0
  },
  {
    "id": 2,
    "name": "Rohan Verma",
    "email": "rohan@example.com",
    "salary": 60000.0
  }
]
```

### Update Employee
**Request:** `PUT /employees/1`
```json
{
  "name": "Aarzoo Sharma",
  "email": "aarzoo@example.com",
  "salary": 55000.0
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Aarzoo Sharma",
  "email": "aarzoo@example.com",
  "salary": 55000.0
}
```

### Delete Employee
**Request:** `DELETE /employees/1`

**Response:** `200 OK`
```
Employee deleted successfully
```

### Error Response — Validation Failure
**Request:** `POST /employees` with a blank name
```json
{
  "name": "",
  "email": "aarzoo@example.com",
  "salary": 50000.0
}
```

**Response:** `400 Bad Request`
```json
{
  "timestamp": "2026-07-08T10:15:30",
  "status": 400,
  "message": "Validation failed",
  "path": "/employees",
  "errors": {
    "name": "Name is required"
  }
}
```

### Error Response — Employee Not Found
**Request:** `GET /employees/99`

**Response:** `404 Not Found`
```json
{
  "timestamp": "2026-07-08T10:20:00",
  "status": 404,
  "message": "Employee not found with id: 99",
  "path": "/employees/99"
}
```
