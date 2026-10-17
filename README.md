# High-Throughput Ticket Booking System (Flash Sale)
> An Event-Driven Microservices Learning Project powered by Apache Kafka (KRaft), Spring Boot 3, and PostgreSQL.

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.4](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.8%20(KRaft)-black.svg)](https://kafka.apache.org/)
[![Docker Compose](https://img.shields.io/badge/Docker%20Compose-Ready-blue.svg)](https://docs.docker.com/compose/)

---

## Project Overview

This repository is a hands-on architectural project created to explore and practice **Apache Kafka**, **Event-Driven Architecture (EDA)**, and **Multi-Module Microservices** using modern Spring Boot.

### The Business Scenario: High-Throughput Flash Sale
Imagine a high-demand ticketing platform (such as a major concert tour or championship final) where **thousands of users attempt to reserve seats within the exact same minute**. 
Instead of relying on synchronous, blocking HTTP calls that easily fail under high concurrency, this project implements an asynchronous, distributed event-driven pipeline using Kafka to decouple services, guarantee ordering, and eliminate race conditions.

---

## Architecture & Event Flow

```text
                      [ Client / Load Generator ]
                                   │
                                   │ 1. HTTP POST /api/orders
                                   ▼
        ┌─────────────────────────────────────────────────────┐
        │                 ORDER SERVICE (8081)                │
        │  - REST API Endpoint (202 Accepted)                 │
        │  - PostgreSQL DB (Status: PENDING)                  │
        │  - MapStruct DTO-to-Event conversion                │
        │  - Kafka Producer (Partition Key = eventId)         │
        └──────────────────────────┬──────────────────────────┘
                                   │
                                   │ 2. OrderPlacedEvent
                                   ▼
                       ╔═══════════════════════════╗
                       ║ TOPIC: order-placed-events║
                       ║ (6 Partitions, KRaft Mode)║
                       ╚═══════════════════════════╝
                                   │
                                   │ 3. Asynchronous Consumption
                                   ▼
        ┌─────────────────────────────────────────────────────┐
        │                PAYMENT SERVICE (8082)               │
        │  - Consumer Group: payment-processors               │
        │  - Idempotency Deduplication (prevent double charge)│
        │  - Non-blocking Retry & Dead Letter Topic (DLT)     │
        └──────────────────────────┬──────────────────────────┘
                                   │
                                   │ 4. PaymentCompletedEvent
                                   ▼
                     ╔═══════════════════════════════╗
                     ║TOPIC: payment-completed-events║
                     ╚═══════════════════════════════╝
                                   │
                                   │ 5. Asynchronous Notification
                                   ▼
        ┌─────────────────────────────────────────────────────┐
        │             NOTIFICATION SERVICE (8083)             │
        │  - Consumer Group: notification-dispatchers         │
        │  - Generates PDF ticket and sends confirmation email│
        └─────────────────────────────────────────────────────┘
```

---

## Key Architectural Concepts

* **Event-Driven Choreography**: Services are completely decoupled. `order-service` has no direct dependency on `payment-service`; services communicate strictly through immutable events.
* **Partitioning & Strict Ordering**: Messages are partitioned by `eventId`. All ticket purchases for the same event land on the **same Kafka partition**, ensuring strictly sequential FIFO processing without locking the entire database.
* **Modern Kafka KRaft Mode**: No ZooKeeper required. Runs natively on the modern Raft consensus protocol.
* **Consumer Group Scaling**: Topics are configured with 6 partitions, allowing horizontal scaling up to 6 parallel consumer instances.
* **Resilience & DLT (Dead Letter Topic)**: Failed events (e.g., rejected payments, invalid payloads) are retried with exponential backoff and routed to a dedicated DLT instead of blocking healthy traffic (Head-of-Line Blocking).
* **Consumer Idempotency**: Prevents duplicate charges if network retries cause event re-deliveries.

---

## Multi-Module Structure

```text
flash-sale-kafka/
├── common-events/          # Shared immutable event records & DTO contracts (No Spring dependency)
├── order-service/          # REST API, PostgreSQL persistence, MapStruct, and Kafka Producer
├── payment-service/        # Event Consumer, payment simulation, idempotency, and DLT
├── notification-service/   # Separate Consumer Group for mock email/PDF ticket dispatch
└── load-generator/         # Load testing utility blasting thousands of concurrent orders
```

---

## Tech Stack

* **Language**: Java 21 LTS
* **Framework**: Spring Boot 3.4.5
* **Messaging**: Apache Kafka (KRaft mode via Confluent `cp-kafka:7.9.0`)
* **Database**: PostgreSQL 17
* **Data Mapping**: MapStruct 1.6
* **Boilerplate Reduction**: Project Lombok
* **Infrastructure**: Docker Compose & Kafka-UI

---

## Getting Started

### 1. Prerequisites
* JDK 21+
* Apache Maven 3.9+
* Docker Desktop

### 2. Start Infrastructure
Run Kafka, PostgreSQL, and Kafka-UI via Docker Compose:
```bash
docker compose up -d
```
* **Kafka Broker**: `localhost:9092`
* **PostgreSQL**: `localhost:5432` (`flashsale` db)
* **Kafka-UI Dashboard**: [http://localhost:9080](http://localhost:9080)

### 3. Build & Run
Build all modules:
```bash
mvn clean compile
```

Run `OrderServiceApplication` (Port `8081`):
```bash
mvn spring-boot:run -pl order-service
```

### 4. Create an Order
```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "user-42",
    "eventId": "concert-waw-2026",
    "seatNumber": "A12",
    "amount": 250.00
  }'
```
Response: `202 ACCEPTED` with a generated `UUID` tracking ID. You can inspect the message in [Kafka-UI](http://localhost:9080) under the `order-placed-events` topic.

---

## Learning Roadmap

- [x] **Phase 1**: Project skeleton, multi-module setup, Docker Compose (KRaft Kafka + PostgreSQL + Kafka-UI).
- [x] **Phase 2**: `order-service` REST API, MapStruct, and Kafka producer with `eventId` partition key.
- [ ] **Phase 3**: `payment-service` Kafka consumer with idempotent processing and Dead Letter Queue (`@RetryableTopic`).
- [ ] **Phase 4**: `notification-service` consumer group for email/ticket delivery.
- [ ] **Phase 5**: Spike load test using `load-generator` and consumer lag observation in Kafka-UI.
- [ ] **Phase 6**: Optional packaging into Docker containers and Kubernetes manifests (`deployment.yaml`).

---

## Author
Hands-on educational project exploring enterprise event-driven architectures with Apache Kafka and Spring Boot.