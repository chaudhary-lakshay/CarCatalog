<div align="center">

# 🚗 CarCatalog - Vehicle Rental Management System

[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-Auth-purple?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Swagger](https://img.shields.io/badge/Swagger-API_Docs-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](https://swagger.io/)

**A comprehensive vehicle catalog and rental management system with secure RESTful API**

[Overview](#-overview) •
[Features](#-key-features) •
[Architecture](#-architecture) •
[Installation](#-installation) •
[API Reference](#-api-reference) •
[Security](#-security) •
[Documentation](#-documentation) •
[Contributing](#-contributing)

</div>

## 📋 Overview

**CarCatalog** is a robust Spring Boot application designed to provide a complete solution for vehicle catalog and rental management. The system allows users to browse available vehicles, make reservations, manage bookings, and process payments through a secure RESTful API.

The application supports both individual and corporate customers, with different workflows for each. It includes features for tracking vehicle maintenance, crash history, and city-based operations. The system also handles additional services that can be added to rentals, such as GPS or child seats.

## ✨ Key Features

| 🚗 **Vehicle Management** | 🎟️ **Reservation System** | 🔐 **Security** |
|:---|:---|:---|
| • Browse & search vehicles<br>• Filter by brand, color, etc.<br>• Vehicle maintenance tracking<br>• Crash history management<br>• Kilometer tracking | • Booking management<br>• Date-based availability<br>• City-based pickup/return<br>• Different pricing for different cities<br>• Individual & corporate customers | • JWT authentication<br>• Role-based access control<br>• Admin-specific endpoints<br>• Secure password storage<br>• Token-based authorization |

### 🌟 Advanced Features

| 💳 **Payment Processing** | 🧾 **Invoicing** | 🎫 **Additional Services** |
|:---|:---|:---|
| • Stripe PaymentIntents integration<br>• Credit card storage option (CVV never stored)<br>• Secure transaction handling | • Automatic invoice generation<br>• Detailed price breakdown<br>• Unique invoice numbers<br>• Rental history tracking | • Add-on services for rentals<br>• Priced per day<br>• Can be added during or after booking<br>• Included in invoice calculations |

## 🗄️ Architecture

### Technology Stack

- **Backend**: Java 17, Spring Boot 3.x, Spring Data JPA
- **Database**: MySQL 8.0
- **Security**: Spring Security, JWT Authentication
- **API Documentation**: Swagger OpenAPI 3.0
- **Utilities**: Lombok, ModelMapper
- **Testing**: JUnit 5

### Project Structure

The project follows a layered architecture with clear separation of concerns:

- **API Layer**: Controllers handling HTTP requests
- **Business Layer**: Services implementing business logic
- **Data Access Layer**: Repositories for database operations
- **Entity Layer**: Domain models representing database tables
- **DTO Layer**: Data Transfer Objects for API responses
- **Request Models**: Objects for handling API requests
- **Core Utilities**: Common functionality and helpers

## 📥 Installation

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

### Quick Start Guide

1. **Clone the repository**

```bash
git clone https://github.com/yourusername/CarCatalog.git
cd CarCatalog
```

2. **Configure the database**

```sql
CREATE DATABASE carCatalogDb;
```

3. **Set environment variables**

The application reads configuration from environment variables — no credentials live in the repo.

| Variable | Required | Description |
|----------|----------|-------------|
| `DB_URL` | no | JDBC url, defaults to `jdbc:mysql://localhost:3306/rentACarBackend` |
| `DB_USERNAME` | yes | MySQL username |
| `DB_PASSWORD` | yes | MySQL password |
| `JWT_SECRET` | yes | JWT signing secret — generate with `openssl rand -hex 64` |
| `CORS_ALLOWED_ORIGINS` | no | defaults to `http://localhost:5173` |
| `SPRING_PROFILES_ACTIVE` | no | `dev` (default) or `prod` — prod disables Swagger and SQL logging |

4. **Build and run the application**

```bash
mvn clean install
mvn spring-boot:run
```

5. **Access the application**

- API: [http://localhost:8080](http://localhost:8080)
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 🔥 API Reference

### API Structure

The API is organized into logical controller groups that handle different aspects of the rental system:

1. **Authentication & User Management**: User registration, login, and account management
2. **Customer Management**: Both individual and corporate customer operations
3. **Vehicle Management**: Cars, brands, colors, maintenance, and crash records
4. **Rental Management**: Booking operations and city-based location management
5. **Additional Services**: Extra services that can be added to rentals
6. **Payment & Invoicing**: Payment processing and invoice generation

### Controller Overview

| Controller | Base Path | Purpose | Key Endpoints |
|------------|-----------|---------|---------------|
| **AuthController** | `/api/auth` | Authentication | Login, Registration |
| **UsersController** | `/api/users` | User management | Get users, User details |
| **CustomersController** | `/api/customers` | Base customer operations | List customers, Customer details |
| **IndividualCustomersController** | `/api/individualCustomers` | Person customers | CRUD for individual customers |
| **CorporateCustomersController** | `/api/corporateCustomers` | Business customers | CRUD for corporate customers |
| **CarsController** | `/api/cars` | Car inventory | CRUD for cars |
| **BrandsController** | `/api/brands` | Car brands | CRUD for brands |
| **ColorsController** | `/api/colors` | Car colors | CRUD for colors |
| **CarMaintenancesController** | `/api/carMaintenances` | Maintenance tracking | CRUD for maintenance records |
| **CarCrashesController** | `/api/carCrashes` | Accident history | CRUD for crash records |
| **RentalCarsController** | `/api/rentalCars` | Rental operations | Create/manage rentals, Return cars |
| **CitiesController** | `/api/cities` | Location management | CRUD for pickup/return cities |
| **AdditionalsController** | `/api/additionals` | Extra service types | CRUD for additional service types |
| **OrderedAdditionalsController** | `/api/orderedAdditionals` | Rental extras | Manage extras for specific rentals |
| **PaymentsController** | `/api/payments` | Payment processing | Process payments for different scenarios |
| **CreditCardsController** | `/api/creditCards` | Payment methods | Manage saved credit cards |
| **InvoicesController** | `/api/invoices` | Invoice management | Generate/retrieve invoices |

### Key API Endpoints

#### 🔐 Authentication & Users

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `POST` | `/api/auth/login` | Get JWT token | Public |
| `POST` | `/api/auth/signup` | Register new user | Public |
| `GET` | `/api/users/getAll` | Get all users | Admin |
| `GET` | `/api/users/getById` | Get user by ID | Authenticated |

#### 👤 Customer Management

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `GET` | `/api/customers/getAll` | Get all customers | Authenticated |
| `GET` | `/api/individualCustomers/getAll` | Get all individual customers | Authenticated |
| `POST` | `/api/individualCustomers/add` | Add individual customer | Authenticated |
| `GET` | `/api/corporateCustomers/getAll` | Get all corporate customers | Authenticated |
| `POST` | `/api/corporateCustomers/add` | Add corporate customer | Authenticated |

#### 🚗 Vehicle Management

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `GET` | `/api/cars/getAll` | Get all cars | Public |
| `GET` | `/api/cars/getByCarId` | Get car by ID | Public |
| `POST` | `/api/cars/add` | Add car | Admin |
| `GET` | `/api/brands/getAll` | Get all brands | Public |
| `GET` | `/api/colors/getAll` | Get all colors | Public |
| `GET` | `/api/carMaintenances/getAll` | Get all maintenance records | Authenticated |
| `GET` | `/api/carCrashes/getAll` | Get all crash records | Authenticated |

#### 🎟️ Rental Operations

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `GET` | `/api/rentalCars/getAll` | Get all rentals | Admin |
| `GET` | `/api/rentalCars/getById` | Get rental by ID | Authenticated |
| `POST` | `/api/rentalCars/add` | Create rental | Authenticated |
| `PUT` | `/api/rentalCars/update` | Update rental | Authenticated |
| `PUT` | `/api/rentalCars/receiveTheCar` | Mark car as returned | Authenticated |
| `GET` | `/api/cities/getAll` | Get all cities | Public |

#### 🧩 Additional Services

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `GET` | `/api/additionals/getAll` | Get all additional service types | Public |
| `GET` | `/api/orderedAdditionals/getAll` | Get all ordered additionals | Admin |
| `GET` | `/api/orderedAdditionals/getByOrderedAdditional_RentalCarId` | Get additionals for a rental | Authenticated |

#### 💳 Payment & Invoicing

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `GET` | `/api/payments/getAll` | Get all payments | Admin |
| `POST` | `/api/payments/makePaymentForIndividualRentAdd` | Process payment for individual | Authenticated |
| `POST` | `/api/payments/makePaymentForCorporateRentAdd` | Process payment for corporate | Authenticated |
| `POST` | `/api/payments/makePaymentForOrderedAdditionalAdd` | Pay for additional services | Authenticated |
| `GET` | `/api/invoices/getAll` | Get all invoices | Admin |
| `GET` | `/api/invoices/getAllByCustomer_CustomerId` | Get customer invoice history | Authenticated |
| `GET` | `/api/creditCards/getByCustomerId` | Get customer's saved cards | Authenticated |

## 🔐 Security

| **Security Features** | **Authentication Flow** |
|:---|:---|
| • **JWT Authentication**: Secure token-based authentication<br>• **Password Encryption**: BCrypt encoding<br>• **Role-Based Access Control**: User/admin permissions<br>• **Method-Level Security**: Endpoint protection with @PreAuthorize<br>• **Stateless Sessions**: No server-side session state | 1. User registers or logs in with credentials<br>2. Server validates credentials and returns a JWT token<br>3. Client includes JWT in Authorization header<br>4. Server validates token and grants access based on roles<br><br>**Example Header:**<br>`Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...` |

## 📖 Documentation

### API Documentation

The API is documented using Swagger OpenAPI 3.0, providing interactive documentation for all endpoints.

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **API Docs**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Response Format

All API responses follow a consistent format using wrapper classes:

```json
{
  "success": true,
  "message": "Data listed successfully",
  "data": {"Response data here"}
}
```

Error responses include error details and maintain the same structure:

```json
{
  "success": false,
  "message": "Error message here"
}
```

## 💻 Contributing

Contributions are welcome! Here's how you can contribute:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add some amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

---

<div align="center">
  <p>⭐ Star this repository if you find it helpful!</p>
  <p>
    <sub>CarCatalog - Built with ❤️ using Spring Boot and Java</sub>
  </p>
</div>
