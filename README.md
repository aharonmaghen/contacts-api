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

## 🧪 Testing

### Prerequisites

- Java 17+
- Gradle (or use the included wrapper `./gradlew`)

Then run:

```bash
./gradlew test
```
> Make sure you are running the postgres container for the integration tests
---

## 📊 Metrics

Prometheus-compatible metrics are exposed at:

**[http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)**

---

## 🔐 Security Note

Add:
- Authentication (e.g., JWT, OAuth2)
- Role-based access control