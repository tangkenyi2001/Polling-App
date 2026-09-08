# Mentimeter

A live audience-polling app: create a poll, share it, and watch responses
update in real time as people vote. Supports three poll types — multiple
choice, rating, and word cloud.

## Stack

- **Backend** — Java 21+, Spring Boot (Web MVC, Data JPA, Validation), PostgreSQL
- **Frontend** — React + Vite (`frontend/poll-project`)
- **Real-time updates** — Server-Sent Events (see [`sse-design.md`](sse-design.md))

## Project structure

```
backend/                Spring Boot API
  src/main/java/com/mentimeter/
    controller/          REST endpoints
    service/              business logic
    entity/                JPA entities
    repository/         Spring Data repositories
    dto/                    request/response payloads
    exception/          error handling
    config/                Spring configuration (security, etc.)
  SCHEMA.md              database schema notes

frontend/poll-project/  React + Vite app

dev.sh                 runs backend (:8080) and frontend (:5173) together
erd.puml                entity-relationship diagram (PlantUML)
sse-design.md           design doc for the real-time results stream
```

## Getting started

### Prerequisites

- Java 21+ and Maven (or use the bundled `./mvnw`)
- Node.js + npm
- PostgreSQL running locally with a `polling_app` database

Set up the database connection in
`backend/src/main/resources/application.properties` (defaults to
`jdbc:postgresql://localhost:5432/polling_app`).

### Run everything

```bash
./dev.sh
```

Starts the backend on `:8080` and the frontend on `:5173`, frees those ports
first if something else is already listening, and stops both on Ctrl+C.

### Run individually

```bash
# backend
cd backend && ./mvnw spring-boot:run

# frontend
cd frontend/poll-project && npm install && npm run dev
```

## API overview

All endpoints are under `/api`:

| Resource  | Endpoints |
|-----------|-----------|
| Users     | `POST /users/register`, `POST /users/login`, `GET /users/{id}` |
| Polls     | `POST /polls/{mcq,rating,wordcloud}`, `GET /polls/{id}`, `GET /polls/{id}/options`, `GET /polls/owner/{ownerId}`, `GET /polls/{id}/results` |
| Responses | `POST /polls/{pollId}/responses/{mcq,rating,wordcloud}`, `GET /polls/{pollId}/responses/{mcq,rating,wordcloud}` |
| Live results | `GET /polls/{id}/stream` — Server-Sent Events stream of live results, pushed on every new vote |

## Real-time results

Poll results update live via Server-Sent Events rather than WebSockets or
polling — the data only ever needs to flow server → client, so SSE gives
push-latency updates over plain HTTP with free browser reconnect, no socket
management on either side. Full design rationale, flow diagram, and known
limitations are in [`sse-design.md`](sse-design.md); the data model is in
[`erd.puml`](erd.puml) and [`backend/SCHEMA.md`](backend/SCHEMA.md).
