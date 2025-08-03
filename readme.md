# Booking and Load Management System

A Spring Boot application for managing transportation bookings and loads with RESTful APIs.

## Features

- **Load Management**:

  - Create, read, update, and delete loads
  - Filter loads by shipper ID, truck type, and status
  - Pagination and sorting support

- **Booking Management**:
  - Create bookings for available loads
  - Update booking status (PENDING/ACCEPTED/REJECTED)
  - Filter bookings by load ID, transporter ID, and status
  - Automatic load status updates when bookings are made

## Technologies Used

- Java 17
- Spring Boot 3.5.4
- Spring Data JPA
- PostgreSQL
- Lombok
- Spring Validation

## API Endpoints

### Load Management (`/loads`)

| Method | Endpoint          | Description                |
| ------ | ----------------- | -------------------------- |
| GET    | `/loads`          | Get all loads (filterable) |
| GET    | `/loads/{loadId}` | Get a specific load by ID  |
| POST   | `/loads`          | Create a new load          |
| PUT    | `/loads/{loadId}` | Update an existing load    |
| DELETE | `/loads/{loadId}` | Delete a load              |

### Booking Management (`/books`)

| Method | Endpoint          | Description                   |
| ------ | ----------------- | ----------------------------- |
| GET    | `/books`          | Get all bookings (filterable) |
| GET    | `/books/{bookId}` | Get a specific booking by ID  |
| POST   | `/books`          | Create a new booking          |
| PUT    | `/books/{bookId}` | Update an existing booking    |
| DELETE | `/books/{bookId}` | Delete a booking              |

## Entity Models

### Load

```java
@Entity
public class Load {
    private UUID id;
    private String shipperId;
    private String loadingPoint;
    private String unloadingPoint;
    private Instant loadingDate;
    private Instant unloadingDate;
    private String productType;
    private String truckType;
    private int noOfTrucks;
    private double weight;
    private String comment;
    private Instant datePosted;
    private LoadStatus status; // POSTED, BOOKED, CANCELLED
}

@Entity
public class Booking {
    private UUID id;
    @ManyToOne
    private Load load;
    private String transporterId;
    private double proposedRate;
    private String comment;
    private BookingStatus status; // PENDING, ACCEPTED, REJECTED
    private Instant requestedAt;
}

```

## Setup Instructions

1. **Clone the repository:**

   ```bash
   git clone https://github.com/dishant987/Booking-apis.git
   ```

2. **Configure PostgreSQL database in `application.properties`:**

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/booking_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **Build and run the application:**

   ```bash
   mvn spring-boot:run
   ```
