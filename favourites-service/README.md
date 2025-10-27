# M321 - Favourites Service Component

**Responsible Developer:** Oscar Doell

---

## 1. Component Purpose

The **Favourites Service** is a microservice responsible for managing the relationship between users and recipes. Its sole purpose is to store, retrieve, and count "favourite" actions.

It decouples the "favourite" logic from the User and Recipe services, allowing them to remain focused on their own domains. This service answers two primary questions:
1.  Which recipes has a specific user favourited?
2.  How many users have favourited a specific recipe?

## 2. Technology Stack

* **Language:** Java 17
* **Framework:** Spring Boot 3
* **Data Access:** Spring Data JPA (Hibernate)
* **Database:** PostgreSQL
* **Runtime:** Docker
* **Testing:** JUnit 5, Mockito, Testcontainers

## 3. Database Schema

This service uses its own dedicated PostgreSQL database.

* **Table:** `favourites`

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | **Primary Key** | Unique identifier for the favourite entry. |
| `user_id` | `BIGINT` | `NOT NULL` | The ID of the user (links to the User service). |
| `recipe_id` | `BIGINT` | `NOT NULL` | The ID of the recipe (links to the Recipe service). |
| `timestamp` | `TIMESTAMP`| `NOT NULL` | Timestamp of when the favourite was created. |

## 4. API Specification (Interface)

This is the public contract that other services in the system use to interact with the Favourites Service.

| Method | Path | Description | Request Body (JSON) | Response (JSON) |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/favourites` | Adds a recipe to a user's favourites list. | `{"userId": 1, "recipeId": 100}` | `200 OK` (Returns the new `Favorite` object) |
| `DELETE` | `/favourites/{id}` | Removes a favourite by its unique entry ID. | (none) | `200 OK` (No content) |
| `GET` | `/favourites/user/{userId}` | Lists all favourite entries for a specific user. | (none) | `200 OK` (Returns `List<Favorite>`) |
| `GET` | `/favourites/recipe/{recipeId}` | Counts how many users have favourited a specific recipe. | (none) | `200 OK` (Returns a `long` number) |

**Example `Favorite` Object (JSON):**
```json
{
  "id": 1,
  "userId": 1,
  "recipeId": 100,
  "timestamp": "2025-10-27T16:00:00.000000"
}
```

## 5. Runtime Environment (Docker)

The service is fully containerized and designed to be run with Docker Compose.

**Prerequisites:**
* Docker and Docker Compose are installed.
* The Docker daemon is running.

**How to Run:**
1.  From the project's root directory, run:
    ```bash
    docker-compose up --build
    ```
2.  The service will build, start, and connect to its dedicated PostgreSQL database.
3.  The API will be available at: `http://localhost:8081`

**How to Stop:**
1.  Press `Ctrl+C` in the terminal running the logs.
2.  To remove the containers, run: `docker-compose down`

## 6. High Availability (HA) Considerations

This component is designed to support the system's HA goals:

1.  **Service Replication:** The service is stateless (all state is in the DB). Multiple instances can be run behind a load balancer to ensure redundancy.
2.  **Health Check:** The service exposes a Spring Boot Actuator endpoint at `/actuator/health` (not yet configured) that a load balancer can use to verify the service is alive.
3.  **Database Redundancy:** The service relies on the PostgreSQL database. For full HA, the database itself should be configured with replication (e.g., a primary and at least one standby).
