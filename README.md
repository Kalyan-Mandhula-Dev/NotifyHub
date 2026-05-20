# NotifyHub

A multi-tenant event-driven notification platform built with microservices.

## Tech Stack
- Java 17 + Spring Boot 3
- Apache Kafka
- MySQL
- Docker
- AWS S3, EC2, IAM
- React

## Architecture
5 independent microservices communicating via Kafka:
- **auth-service** – JWT authentication
- **user-service** – Tenant management, subscriptions, API keys
- **event-service** – Receives and publishes events to Kafka
- **notification-service** – Consumes Kafka events, sends email/webhook
- **template-service** – Manages notification templates via AWS S3

## Running Locally

### Prerequisites
- Docker Desktop installed
- Java 17
- Maven

### Start user-service
```bash
docker-compose up --build
```

Service runs on `http://localhost:8082`

## API Endpoints

### Tenants
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/users/tenants | Create tenant |
| GET | /api/users/tenants/{id} | Get tenant |
| PUT | /api/users/tenants/{id} | Update tenant |
| DELETE | /api/users/tenants/{id} | Deactivate tenant |

### Subscriptions
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/users/subscriptions | Add subscription |
| GET | /api/users/subscriptions/{tenantId} | Get subscriptions |
| DELETE | /api/users/subscriptions/{id} | Delete subscription |

### API Keys
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/users/apikeys/{tenantId} | Generate API key |
| GET | /api/users/apikeys/{tenantId} | Get active key |