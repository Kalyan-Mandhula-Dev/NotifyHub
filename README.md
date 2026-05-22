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

### Start all services
```bash
docker-compose up --build
```

| Service | Port |
|---------|------|
| auth-service | http://localhost:8081 |
| user-service | http://localhost:8082 |

---

## API Endpoints

### Auth Service

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/auth/register | Register new tenant | No |
| POST | /api/auth/login | Login and get JWT token | No |
| GET | /api/auth/validate | Validate JWT token | Yes |

#### Register — Request Body
```json
{
  "companyName": "Swiggy",
  "email": "tech@swiggy.com",
  "password": "Swiggy@123"
}
```

#### Login — Request Body
```json
{
  "email": "tech@swiggy.com",
  "password": "Swiggy@123"
}
```

#### Login / Register — Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tenantId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "email": "tech@swiggy.com",
  "message": "Registration successful"
}
```

#### Validate — Header
```
Authorization: Bearer <token>
```

---

### User Service

#### Tenants
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/users/tenants | Create tenant |
| GET | /api/users/tenants/{id} | Get tenant |
| PUT | /api/users/tenants/{id} | Update tenant |
| DELETE | /api/users/tenants/{id} | Deactivate tenant |

#### Subscriptions
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/users/subscriptions | Add subscription |
| GET | /api/users/subscriptions/{tenantId} | Get subscriptions |
| DELETE | /api/users/subscriptions/{id} | Delete subscription |

#### API Keys
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/users/apikeys/{tenantId} | Generate API key |
| GET | /api/users/apikeys/{tenantId} | Get active key |

---

## Service Communication

```
POST /api/auth/register
        ↓
   auth-service  ──────calls──────▶  user-service
   saves to auth_db                  saves to user_db
        ↓
   returns JWT token
```

auth-service calls user-service directly on registration to create the tenant profile. All other inter-service communication goes through Kafka (coming with event-service).

---

## Database

Each service owns its own database. No shared tables.

| Service | Database |
|---------|----------|
| auth-service | auth_db |
| user-service | user_db |

---

## Progress

- [x] user-service
- [x] auth-service
- [ ] event-service
- [ ] notification-service
- [ ] template-service
- [ ] React frontend
- [ ] AWS deployment