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
---

# Coursework Report – Questions & Answers

*(This section includes answers to all coursework questions as required in the specification)*

---

## Part 1: Service Architecture & Setup

### Question 1: JAX-RS Resource Lifecycle

**Answer:**

By default, JAX-RS creates a new instance of the resource class for each incoming request. This means each request is handled independently, and there is no shared state inside the resource class itself.

Because of this, the in-memory data (like rooms and sensors) cannot be stored inside resource classes. Instead, I used a shared `MockDatabase` with static maps to store data so that it persists across requests.

This approach avoids data loss, but it also means that if multiple requests happen at the same time, there could be potential concurrency issues. However, for this coursework, using simple in-memory structures like `Map` is acceptable.

---

### Question 2: Discovery Endpoint & HATEOAS

**Answer:**

Hypermedia (HATEOAS) is important because it allows the API to provide links to available resources inside the response itself. Instead of the client needing to know all endpoints in advance, it can discover them dynamically.

In my implementation, the discovery endpoint returns links to `/rooms` and `/sensors`, which helps guide the client on how to interact with the API.

This approach makes the API more flexible and easier to use, especially if endpoints change in the future, because the client can rely on the links instead of hardcoding paths.

---

## Part 2: Room Management

### Question 3: Returning IDs vs Full Objects

**Answer:**

Returning only IDs reduces the size of the response, which improves network efficiency and reduces bandwidth usage. However, it requires the client to make additional requests to fetch full details for each room.

Returning full room objects, as done in my implementation, provides all necessary information in a single response, making it easier for the client to use but slightly increasing the response size.

For this coursework, returning full objects is more practical because it simplifies client-side processing.

---

### Question 4: DELETE Idempotency

**Answer:**

Yes, the DELETE operation is idempotent in my implementation. If a room is deleted successfully the first time, repeating the same DELETE request will not change the state further.

On subsequent requests, the room will no longer exist, so the API will return a `404 Not Found`. The system state remains the same, which satisfies the definition of idempotency.

---

## Part 3: Sensor Operations & Linking

### Question 5: @Consumes(MediaType.APPLICATION_JSON)

**Answer:**

The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells JAX-RS that the API only accepts JSON input for that endpoint. If a client sends data in a different format like `text/plain` or `application/xml`, the request will not be processed.

In this case, JAX-RS automatically rejects the request and returns a `415 Unsupported Media Type`. This ensures that only valid input formats are processed.

---

### Question 6: QueryParam vs Path Parameter

**Answer:**

Using `@QueryParam` is more suitable for filtering because it represents an optional condition applied to a collection, rather than identifying a specific resource.

For example, `/sensors?type=CO2` clearly means filtering results, while `/sensors/type/CO2` treats it like a fixed resource path.

Query parameters are also more flexible and allow combining multiple filters in the future, making the API easier to extend.

---

## Part 4: Deep Nesting with Sub-Resources

### Question 7: Sub-Resource Locator Pattern

**Answer:**

The Sub-Resource Locator pattern helps keep the API structure clean by separating responsibilities into different classes. In my implementation, `SensorResource` handles sensor operations, while `SensorReadingResource` handles readings for a specific sensor.

This avoids having one large controller class and makes the code easier to maintain, test, and extend. It also reflects the real-world relationship where readings belong to a sensor.

---

## Part 5: Error Handling, Exception Mapping & Logging

### Question 8: Why HTTP 422 instead of 404

**Answer:**

HTTP 422 is more appropriate because the request format is correct and sent to a valid endpoint, but the data inside it is invalid.

In my API, when creating a sensor, the `roomId` must exist. If it does not, the request cannot be processed, even though the endpoint itself is valid. Therefore, 422 is more suitable than 404.

---

### Question 9: Security Risks of Stack Traces

**Answer:**

Exposing Java stack traces is a security risk because it reveals internal details such as class names, file paths, and framework information.

Attackers can use this information to understand the system structure and find potential vulnerabilities. To prevent this, my API uses a global exception mapper that returns a generic 500 error message instead of exposing internal details.

---

### Question 10: Why Use Filters for Logging

**Answer:**

Using JAX-RS filters is better because logging is a cross-cutting concern that applies to all endpoints. If logging is added inside every resource method, it leads to repetitive code and poor maintainability.

With filters, logging is handled in one place and automatically applies to all requests and responses, making the code cleaner and more consistent.

---
