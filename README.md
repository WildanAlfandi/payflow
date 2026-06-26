# 💳 PayFlow - Fintech Payment Microservices System

Production-ready fintech payment system built with microservices architecture simulating real-world banking transfer infrastructure.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.x-black)
![Redis](https://img.shields.io/badge/Redis-7-red)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Next.js](https://img.shields.io/badge/Next.js-15-black)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)

---

## 🏗️ Architecture

┌─────────────────────────────────────────────┐

│              Next.js Frontend               │

│         (localhost:3000)                    │

└──────────────────┬──────────────────────────┘

│

┌──────────────────▼──────────────────────────┐

│           API Gateway (Port 8080)           │

│         Spring Cloud Gateway                │

└──────┬───────────┬───────────┬──────────────┘

│           │           │

┌──────▼───┐ ┌────▼─────┐ ┌───▼──────────┐

│  User    │ │ Account  │ │ Transaction  │

│ Service  │ │ Service  │ │   Service    │

│  :8081   │ │  :8082   │ │    :8083     │

└──────────┘ └────┬─────┘ └───▲──────────┘

│ Kafka      │ Kafka

└─────┬──────┘

│

┌─────────▼────────┐

│   Notification   │

│     Service      │

│      :8084       │

└──────────────────┘

---

## ✨ Key Features

- 🔐 **JWT Authentication** — Stateless auth flowing through API Gateway
- 💸 **Real-time Transfer** — Atomic debit/credit with transaction consistency
- 🔁 **Idempotency** — Redis-based duplicate transfer prevention
- ⚡ **Balance Caching** — Redis cache reducing DB load on frequent reads
- 📨 **Event-Driven** — Kafka async messaging between services
- 🔔 **Auto Notification** — DEBIT/CREDIT notifications per transaction
- 🐳 **Docker Compose** — One-command full stack deployment

---

## 🛠️ Tech Stack

payflow/

├── api-gateway/          # Spring Cloud Gateway (port 8080)

├── user-service/         # Auth, JWT (port 8081)

├── account-service/      # Balance, Transfer, Redis (port 8082)

├── transaction-service/  # History, Kafka Consumer (port 8083)

├── notification-service/ # Notif, Kafka Consumer (port 8084)

├── frontend/             # Next.js Dashboard (port 3000)

├── docker-compose.yml    # Full stack orchestration

└── init-db.sql           # Database initialization

| Layer | Technology |
|---|---|
| Backend | Java Spring Boot 3.x |
| API Gateway | Spring Cloud Gateway |
| Message Broker | Apache Kafka |
| Cache | Redis |
| Database | PostgreSQL (4 separate DBs) |
| Frontend | Next.js 15 + TypeScript |
| Styling | Tailwind CSS |
| Containerization | Docker + Docker Compose |
| Security | JWT + Spring Security |

---

## 📁 Project Structure
payflow/

├── api-gateway/          # Spring Cloud Gateway (port 8080)

├── user-service/         # Auth, JWT (port 8081)

├── account-service/      # Balance, Transfer, Redis (port 8082)

├── transaction-service/  # History, Kafka Consumer (port 8083)

├── notification-service/ # Notif, Kafka Consumer (port 8084)

├── frontend/             # Next.js Dashboard (port 3000)

├── docker-compose.yml    # Full stack orchestration

└── init-db.sql           # Database initialization

---

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose
- Node.js 18+ (for frontend)

### Run with Docker Compose

```bash
# Clone the repository
git clone https://github.com/WildanAlfandi/payflow.git
cd payflow

# Start all services
docker-compose up --build
```

### Run Frontend

```bash
cd frontend
npm install
npm run dev
```

### Access

| Service | URL |
|---|---|
| Frontend | http://localhost:3000 |
| API Gateway | http://localhost:8080 |
| User Service | http://localhost:8081 |
| Account Service | http://localhost:8082 |
| Transaction Service | http://localhost:8083 |
| Notification Service | http://localhost:8084 |

---

## 📡 API Endpoints

### Auth (via Gateway :8080)
POST /api/v1/auth/register   # Register user

POST /api/v1/auth/login      # Login & get JWT token

GET  /api/v1/auth/me         # Get profile

### Account
GET  /api/v1/accounts/me                    # Get my account

GET  /api/v1/accounts/{accountNumber}/balance # Get balance

POST /api/v1/accounts/transfer              # Transfer funds

### Transaction
GET /api/v1/transactions?accountNumber=xxx  # Get history

GET /api/v1/transactions/{transactionId}    # Get detail

---

## 🔑 Key Design Patterns

### Idempotency (Duplicate Prevention)
```java
String idempotencyKey = "idempotency:" + request.getIdempotencyKey();
String existing = redisTemplate.opsForValue().get(idempotencyKey);
if (existing != null) {
    throw new DuplicateTransactionException("Duplicate transaction");
}
```

### Event-Driven Flow
Account Service → Kafka (payflow.transaction.completed)

↓

Transaction Service (saves history)

Notification Service (saves notif)

---

## 📸 Screenshots

### Dashboard
![Dashboard](docs/dashboard.png)

### Transfer
![Transfer](docs/transfer.png)

---

## 👨‍💻 Author

**Wildan Alfandi**
- Portfolio: [wildanalfandi.dev](https://wildanalfandi.dev)
- LinkedIn: [linkedin.com/in/wildanalfandi](https://linkedin.com/in/wildanalfandi)
- GitHub: [github.com/WildanAlfandi](https://github.com/WildanAlfandi)
