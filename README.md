# Contacts API

A RESTful API for managing contacts, built with Spring Boot, PostgreSQL, and Docker.

## 🚀 Getting Started

### Prerequisites

Make sure the following are installed on your machine:

- [Git](https://git-scm.com/downloads)
- [Docker Engine + Docker Compose](https://docs.docker.com/get-docker/)
- .env file (use the .env.template)

You can verify installation by running:

```bash
git --version
docker --version
docker-compose --version
```

---

### 🛠️ Run the App

```bash
git clone https://github.com/aharonmaghen/contacts-api.git
cd contacts-api
docker-compose up --build
```

This will start:
- Spring Boot application on `http://localhost:8080`
- PostgreSQL database

> If it's your first time, `--build` ensures Docker images are built correctly.

---

## 📘 API Documentation

Swagger UI is available once the app is running:

**➡️ [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

It provides:
- Interactive API usage
- Request/response schemas
- Error documentation

---

## 🛠️ Accessing pgAdmin

pgAdmin is available at [http://localhost:5050](http://localhost:5050).
Login using the credentials set in your `.env` file:

For example:
```env
PGADMIN_DEFAULT_EMAIL=admin@example.com
PGADMIN_DEFAULT_PASSWORD=admin
```

---

### ➕ Adding a PostgreSQL Server in pgAdmin

After logging into pgAdmin:

1. Click the **“Add New Server”** button on the Dashboard or right-click **"Servers"** in the sidebar.
2. In the **General** tab:
   - **Name**: `Postgres DB` (or anything you like)
3. In the **Connection** tab:
   - **Host name/address**: `db`
   - **Port**: `5432`
   - **Maintenance database**: `contacts`
   - **Username**: `postgres`
   - **Password**: from `.env` → `SPRING_DATASOURCE_PASSWORD`
4. Click **Save** — you’re now connected!

---

## 🧪 Testing

### Prerequisites

- Java 17
- Gradle (or use the included wrapper `./gradlew`)

Then run:

```bash
./gradlew clean test
```
> Make sure you are running the postgres container for the integration tests and have Java 17 installed locally
---

## 📊 Metrics

Prometheus-compatible metrics are exposed at:

**[http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)**

---

## 📜 Logs

You can view logs in two ways:

#### ▶️ 1. Terminal Output

When running via:

```bash
docker-compose up --build
```

Logs will print to the terminal.

#### 🐳 2. Docker Containers

When running:

```bash
docker compose up (...)
```

To view logs for a specific container (e.g. the app):

```bash
docker compose logs <container_name>
```

Or to follow logs live:

```bash
docker compose logs -f <container_name>
```
---

## 🔐 Security Note

Add:
- Authentication (e.g., JWT, OAuth2)
- Role-based access control