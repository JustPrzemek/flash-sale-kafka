# 🎟️ High-Throughput Ticket Booking (Flash Sale) System

System obsługi błyskawicznej sprzedaży biletów na dużą skalę, zbudowany w oparciu o **Apache Kafka (KRaft)**, **Spring Boot 3.4+** i **PostgreSQL**.

---

## 🏗️ Architektura Modułów

| Moduł | Port | Opis |
| :--- | :--- | :--- |
| **`common-events`** | — | Współdzielone rekordy zdarzeń (`OrderPlacedEvent`, `PaymentCompletedEvent`, itp.) |
| **`order-service`** | `8081` | Przyjmowanie rezerwacji, Transactional Outbox, publikacja zdarzeń do tematu `order-placed-events` |
| **`payment-service`** | `8082` | Konsumpcja zdarzeń, idempotencja (`processed_payments`), obsługa błędów i Dead Letter Topic |
| **`notification-service`** | `8083` | Osobna grupa konsumentów (`notification-dispatchers`) symulująca wysyłkę biletów/e-maili |
| **`load-generator`** | `8084` | Narzędzie do symulacji skokowego ruchu (tysiące rezerwacji na sekundę) |

---

## 🚀 Infrastruktura (Docker Compose)

W katalogu znajduje się `docker-compose.yml` z nowoczesnym stosem:
* **Apache Kafka (KRaft)**: port `9092` (brak Zookeepera!)
* **PostgreSQL**: port `5432` (baza `flashsale`)
* **Kafka-UI**: port `9080` (panel graficzny)

Uruchomienie infrastruktury:
```bash
docker compose up -d
```

---

## 🛠️ Kompilacja projektu

W głównym katalogu `flash-sale-kafka`:
```bash
mvn clean compile
```
