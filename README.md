# Grind

Grind is a task and track management system with a split backend and a React frontend. The repository contains the public API gateway, the core business service, the statistics service, the web UI, and local infrastructure for PostgreSQL, Kafka, Keycloak, and ClickHouse.

## What is inside

- `gateway` - public HTTP API, authentication with Keycloak, request routing to backend services, and OpenAPI.
- `core` - business logic for tracks, sprints, and tasks, plus JPA persistence and Kafka outbox/event publishing.
- `statistics` - analytics service that consumes Kafka events and stores/queries data in ClickHouse.
- `frontend` - React + Vite UI for login, registration, workspace, profile, and statistics screens.
- `compose.yaml` - local infrastructure for PostgreSQL, Kafka, Kafdrop, Keycloak, and ClickHouse.
- `task_scenario_curls.sh` - end-to-end curl scenario for smoke testing the API.

## Main capabilities

- Authentication and registration through Keycloak.
- Track CRUD and task CRUD.
- Task planning by sprint or date.
- Marking tasks as complete or returning them to backlog.
- Track and sprint statistics, including raw, per-day, per-week, and actual-state metrics.
- Role-aware access control with JWT.

## Tech Stack

- Java 17
- Spring Boot 3.5.x
- Spring Security
- Spring Kafka
- Spring Data JPA
- PostgreSQL
- ClickHouse
- Keycloak
- React 19
- Vite
- TypeScript
- Tailwind CSS 4

## Architecture

The system is split into three backend services:

- `gateway` is the public entry point on port `8080`.
- `core` handles the domain model and publishes events to Kafka on port `8083`.
- `statistics` consumes events, keeps analytics in ClickHouse, and serves stats endpoints on port `8084`.

The backend communicates through Kafka topics such as `core.request.task`, `core.request.track`, `core.event.task`, `core.event.track`, `statistics.request`, `statistics.event`, and `response`.

ClickHouse tables and views are initialized from `clickhouse/initdb/`.

## Local requirements

- Java 17
- Node.js and npm
- Docker and Docker Compose
- `curl`
- `jq` for the smoke-test script

## Run locally

1. Start infrastructure:

```bash
docker compose -f compose.yaml up -d
```

2. Start the `core` service:

```bash
cd core
./gradlew bootRun
```

3. Start the `statistics` service:

```bash
cd statistics
./gradlew bootRun
```

4. Start the `gateway` service:

```bash
cd gateway
./gradlew bootRun
```

5. Start the frontend:

```bash
cd frontend
npm install
npm run dev
```

The frontend uses `VITE_API_URL` and defaults to `http://localhost:8080`.

## Default ports

| Component | Port |
| --- | ---: |
| Gateway | 8080 |
| Core | 8083 |
| Statistics | 8084 |
| Keycloak | 8085 |
| PostgreSQL for users | 5432 |
| PostgreSQL for core | 5433 |
| ClickHouse HTTP | 8123 |
| Kafdrop | 9000 |
| Frontend dev server | 5173 |

## Keycloak notes

- The local compose setup boots Keycloak with the `administrator` / `administrator` admin account defined in `compose.yaml`.
- The gateway expects a Keycloak realm named `grind`.
- The gateway also expects a client named `grind_client` and uses the client secret from `gateway/src/main/resources/application.yml`.
- JWT roles are mapped from `resource_access` for that client and are exposed to Spring Security with the `ROLE_` prefix.

## API overview

Gateway exposes the main HTTP API:

- `POST /grind/keycloak/token`
- `POST /grind/keycloak/register`
- `POST /grind/keycloak/token/introspect`
- `GET /api/core/track`
- `GET /api/core/track/all`
- `GET /api/core/track/{trackId}`
- `GET /api/core/track/sprints/{trackId}`
- `POST /api/core/track`
- `PUT /api/core/track/{id}`
- `DELETE /api/core/track/{id}`
- `GET /api/core/task/all`
- `GET /api/core/task/{taskId}`
- `GET /api/core/task/sprint/{sprintId}`
- `GET /api/core/task/track/{trackId}`
- `POST /api/core/task`
- `PUT /api/core/task/{id}`
- `PUT /api/core/task/{taskId}/plan/sprint`
- `PUT /api/core/task/{taskId}/plan/date`
- `PUT /api/core/task/{taskId}/complete`
- `PUT /api/core/task/{taskId}/backlog`
- `DELETE /api/core/task/{id}`
- `GET /api/statistics/track/{trackId}/state`
- `GET /api/statistics/track/{trackId}/raw`
- `GET /api/statistics/sprint/{sprintId}`
- `GET /api/statistics/track/{trackId}/per-day`
- `GET /api/statistics/track/{trackId}/per-week`
- `GET /api/statistics/track/{trackId}/per-day/range`
- `GET /api/statistics/track/{trackId}/per-week/range`
- `GET /api/statistics/sync-dbs` for admins

OpenAPI UI is available at `http://localhost:8080/swagger-ui.html`.

## Frontend routes

- `/` - welcome page
- `/login`
- `/register`
- `/home/workspace`
- `/home/workspace/tracks/:trackId`
- `/home/workspace/tracks/:trackId/sprints/:sprintId`
- `/home/workspace/tasks/:taskId`
- `/home/statistics`
- `/profile`

The UI stores the auth session in `localStorage` under `grind.auth`.

## Useful scripts

```bash
./task_scenario_curls.sh
```

The script logs in, creates a track, creates tasks, plans them, completes part of them, and prints the resulting IDs.

## Verification

- Backend tests: run `./gradlew test` inside `core`, `gateway`, or `statistics`.
- Frontend checks: run `npm run build` and `npm run lint` inside `frontend`.
