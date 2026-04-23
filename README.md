# Smart Campus API

## Overview

The Smart Campus API is a RESTful web service built using Java (JAX-RS) and deployed on Apache Tomcat.
It provides endpoints to manage campus rooms, IoT sensors, and sensor readings.

The system follows REST principles, including resource-based URLs, proper HTTP methods, stateless design, and structured error handling.

---

## Architecture

The project is structured into the following packages:

```
com.mycompany.smartcampus
├── config        (Application configuration)
├── resource      (REST endpoints)
├── model         (Data models)
├── store         (In-memory data storage)
├── exception     (Custom exceptions + mappers)
├── filter        (Logging filter)
```

The API uses an in-memory `MockDatabase` instead of a real database as required by the coursework.

---

## Features

- Room Management (Create, Retrieve, Delete)
- Sensor Management with filtering
- Sensor Readings (nested sub-resource)
- Discovery Endpoint for API navigation
- Exception Handling using custom mappers
- Logging filter for request/response tracking

---

## Discovery Endpoint

### GET `/api/v1`

Returns API metadata and available resources.

```bash
curl http://localhost:8080/SmartCampusAPI/api/v1
```

### Example Response

```json
{
  "apiName": "Smart Campus Sensor API",
  "version": "v1",
  "resources": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  }
}
```

---

## Room Management

### Get All Rooms

```bash
curl http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### Get Room by ID

```bash
curl http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

### Create Room

```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id":"NEW-101","name":"New Room","capacity":50}'
```

### Delete Room

```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

A room cannot be deleted if it still has sensors assigned.

---

## Sensor Management

### Get All Sensors

```bash
curl http://localhost:8080/SmartCampusAPI/api/v1/sensors
```

### Filter Sensors by Type

```bash
curl http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=Temperature
```

### Create Sensor

```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 22.5,
  "roomId": "LIB-301"
}'
```

---

## Sensor Readings (Sub-resource)

### Get All Readings

```bash
curl http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings
```

### Add Reading

```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
-H "Content-Type: application/json" \
-d '{"value": 25.5}'
```

Automatically:

- Generates reading ID (UUID)
- Adds timestamp
- Updates sensor’s current value

---

## Error Handling

The API uses custom exception mappers to return structured JSON responses.

### Example Error

```json
{
  "errorMessage": "Referenced room does not exist.",
  "errorCode": 422,
  "documentation": "/api/v1"
}
```

### Supported Error Cases

- 409 → Room not empty
- 422 → Invalid linked resource
- 403 → Sensor unavailable (maintenance)
- 500 → Generic server error

---

## Design Decisions

### 1. Why Query Parameters?

Query parameters are used for filtering (e.g., `/sensors?type=Temperature`) because they provide flexibility and keep URLs clean without creating multiple endpoints.

---

### 2. Why Sub-resources?

Sensor readings are implemented as sub-resources:

```
/sensors/{sensorId}/readings
```

This reflects the hierarchical relationship between sensors and their readings.

---

### 3. Why Use IDs Instead of Nested Objects?

Only IDs are used (e.g., `roomId`) instead of embedding full objects to:

- reduce redundancy
- improve performance
- simplify relationships

---

### 4. Hypermedia (HATEOAS)

The discovery endpoint provides navigation links to available resources, enabling clients to dynamically explore the API instead of relying on static documentation.

---

## Technologies Used

- Java (JAX-RS / Jakarta EE)
- Maven
- Apache Tomcat
- JSON (JAXB / Jackson)

---

## How to Run

1. Open project in NetBeans
2. Configure Apache Tomcat server
3. Run the project
4. Access API via:

```
http://localhost:8080/SmartCampusAPI/api/v1
```

---

## Testing

You can test the API using:

- Postman
- curl (examples provided above)

---

## Notes

- No database is used (in-memory storage via `MockDatabase`)
- Designed according to REST principles
- Suitable for demonstration and coursework purposes

---
