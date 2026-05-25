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
| event-service | http://localhost:8083 |
| kafka | localhost:9092 |

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

### Event Service

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/events/trigger | Trigger a notification event | No |
| GET | /api/events/{tenantId} | Get all events for a tenant | No |

#### Trigger Event — Request Body
```json
{
  "tenantId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "eventType": "order.placed",
  "channel": "EMAIL",
  "recipient": "rahul@gmail.com",
  "payload": {
    "orderId": "SWG123",
    "restaurantName": "Biryani House",
    "amount": 349
  }
}
```

#### Trigger Event — Response
```json
{
  "id": 1,
  "tenantId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "eventType": "order.placed",
  "channel": "EMAIL",
  "recipient": "rahul@gmail.com",
  "status": "PUBLISHED",
  "createdAt": "2026-05-17T10:30:00"
}
```

#### Channel Types
| Value | Description |
|-------|-------------|
| EMAIL | Sends notification to an email address |
| WEBHOOK | Posts notification payload to a webhook URL |

#### Event Status Values
| Status | Description |
|--------|-------------|
| RECEIVED | Event saved to DB, not yet sent to Kafka |
| PUBLISHED | Successfully published to Kafka topic |
| FAILED | Failed to publish to Kafka |

#### Kafka Topics
| Topic | Used For |
|-------|----------|
| notification.email.send | Email notification events |
| notification.webhook.send | Webhook notification events |

---

## Service Communication

```
POST /api/auth/register
        ↓
   auth-service  ──────calls──────▶  user-service
   saves to auth_db                  saves to user_db
        ↓
   returns JWT token


POST /api/events/trigger
        ↓
   event-service
   validates tenant via user-service
   saves event to event_db
        ↓
   publishes to Kafka topic
        ↓
   returns 202 Accepted immediately
        ↓ (async)
   notification-service (coming next)
   consumes from Kafka topic
```

auth-service and event-service call user-service directly via HTTP for tenant validation.
All notification processing is asynchronous via Kafka — event-service does not wait for the notification to be delivered.

---

## Transactional Outbox Pattern

event-service implements the Transactional Outbox Pattern to guarantee no events are lost.

```
Trigger received
      ↓
Save event + outbox record in ONE database transaction
      ↓
Try to publish to Kafka immediately
      ↓ (if Kafka is down)
Outbox scheduler retries every 30 seconds
until published successfully
```

This ensures events are never silently dropped even if Kafka is temporarily unavailable.

---

## Database

Each service owns its own database. No shared tables.

| Service | Database |
|---------|----------|
| auth-service | auth_db |
| user-service | user_db |
| event-service | event_db |

---

## Progress

- [x] user-service
- [x] auth-service
- [x] event-service
- [ ] notification-service
- [ ] template-service
- [ ] React frontend
- [ ] AWS deployment