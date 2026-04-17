# Inventory Management and Reporting System | Infosys SpringBoard Internship Project

A full-stack inventory management application built as part of the **Infosys Springboard Virtual Internship**. The backend is a Spring Boot REST API secured with JWT authentication, and the frontend is a React SPA with role-based routing, real-time low-stock alerts, and analytics dashboards.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Architecture Overview](#architecture-overview)
- [Database Schema](#database-schema)
- [API Reference](#api-reference)
- [Security & Roles](#security--roles)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [1. Database Setup](#1-database-setup)
  - [2. Email Configuration](#2-email-configuration)
  - [3. Backend Setup](#3-backend-setup)
  - [4. Frontend Setup](#4-frontend-setup)
- [Configuration Reference](#configuration-reference)
- [Running Tests](#running-tests)
- [Known Issues & Notes](#known-issues--notes)

---

## Features

### Backend
- **JWT Authentication** — Stateless token-based auth using JJWT 0.11.5 (HS256, 24-hour expiry)
- **Role-Based Access Control** — Three roles: `ADMIN`, `SUPPLIER`, `CUSTOMER`
- **Product Management** — Full CRUD with soft-delete (deactivate) and hard-delete
- **Stock Control** — Increase and reduce stock with validation (prevents negative stock)
- **Product Search & Filtering** — By name (partial, case-insensitive), category, or supplier
- **Low-Stock Email Alerts** — Triggered automatically on stock reduction and on demand via the report endpoint; sends a formatted email listing all products below their reorder level
- **Seed Data on Startup** — Categories and suppliers are auto-seeded if the tables are empty (`StarterDataConfig`)
- **Input Validation** — Service-layer validators for product fields and stock quantities

### Frontend
- **Two-panel login/register UI** — Branding left, form right; supports dark/light theme toggle
- **Dashboard** — Summary cards (total products, inventory value, low-stock count, category count), bar chart of products by category, live low-stock panel, and toast notifications on load
- **Products page** — Full product table with search across name/SKU/category/supplier, inline edit form (SUPPLIER+), and delete (ADMIN only)
- **Add Product page** — Form with validation, restricted to SUPPLIER role and above
- **Reports page** — Four tabs: Summary, Low Stock, Value Report (sorted by total value), and Category Breakdown — all computed client-side from the product list
- **Add User page** — Admin-only form to register new user accounts
- **Dark/Light theme** — Persisted to `localStorage`, toggled from the navbar and login pages
- **Toast notification system** — Stacked, auto-dismissed toasts for success/error/warning/info
- **Role hierarchy enforcement** — `ADMIN ≥ SUPPLIER ≥ CUSTOMER`; nav items and action buttons are conditionally rendered per role

---

## Tech Stack

### Backend
| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2.5 |
| Security | Spring Security 6, JJWT 0.11.5 |
| Persistence | Spring Data JPA, Hibernate 6, MySQL 8 |
| Validation | Jakarta Bean Validation |
| Email | Jakarta Mail (SMTP via Gmail) |
| Utilities | Lombok |
| Testing | JUnit 5, Mockito, Spring MockMvc, Spring Security Test |
| Build | Maven (spring-boot-starter-parent 3.2.5) |

> **Note:** The pom.xml targets Java 21. If you are running Java 23, upgrade Spring Boot to `3.3.5` to avoid a Byte Buddy incompatibility with Mockito's inline mocking during tests.

### Frontend
| Layer | Technology |
|---|---|
| Language | JavaScript (ES2022) |
| Framework | React 18.2 |
| Routing | React Router v6 |
| HTTP | Axios 1.15 |
| Charts | Custom canvas-based bar chart (no external chart library) |
| Styling | Plain CSS with CSS custom properties (dark/light themes via `data-theme`) |
| Build | Create React App (react-scripts 5) |

---

## Project Structure

```
.
├── backend/                          # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/
│   │   │   │   ├── database/
│   │   │   │   │   └── DatabaseConnection.java       # Legacy JDBC utility (unused by JPA layer)
│   │   │   │   └── inventory/
│   │   │   │       ├── ServerApplication.java         # Entry point; @EntityScan / @EnableJpaRepositories
│   │   │   │       ├── config/
│   │   │   │       │   └── StarterDataConfig.java     # Seeds categories & suppliers on first start
│   │   │   │       ├── controller/
│   │   │   │       │   ├── Controller.java            # /api/** — products, stock, reports
│   │   │   │       │   └── AuthController.java        # /auth/register, /auth/login
│   │   │   │       ├── authservice/
│   │   │   │       │   ├── AuthService.java           # Register & login business logic
│   │   │   │       │   └── CustomUserDetailsService.java  # Loads User by username for Spring Security
│   │   │   │       ├── security/
│   │   │   │       │   ├── WebSecurityConfig.java     # Filter chain, route permissions
│   │   │   │       │   ├── SecurityBeansConfig.java   # PasswordEncoder, AuthProvider, AuthManager
│   │   │   │       │   ├── JwtAuthenticationFilter.java  # Extracts & validates JWT on every request
│   │   │   │       │   └── CorsConfig.java            # CORS: allows http://localhost:3000
│   │   │   │       ├── utils/
│   │   │   │       │   └── AuthUtil.java              # JWT generate / validate / extract claims
│   │   │   │       ├── entity/
│   │   │   │       │   ├── User.java                  # Spring Security UserDetails + Lombok @Builder
│   │   │   │       │   └── Role.java                  # Enum: ADMIN, SUPPLIER, CUSTOMER
│   │   │   │       ├── repository/
│   │   │   │       │   └── UserRepository.java        # JPA repo for users table
│   │   │   │       ├── dto/
│   │   │   │       │   ├── AuthRequest.java           # { username, password }
│   │   │   │       │   ├── AuthResponse.java          # { token, username, role }
│   │   │   │       │   └── RegisterRequest.java       # { username, password, email, role }
│   │   │   │       ├── database_system/
│   │   │   │       │   ├── entity/
│   │   │   │       │   │   ├── Product.java           # Core inventory entity
│   │   │   │       │   │   ├── Category.java          # Product category
│   │   │   │       │   │   ├── Supplier.java          # Supplier contact info
│   │   │   │       │   │   └── Transaction.java       # Stock movement history record
│   │   │   │       │   └── repository/
│   │   │   │       │       ├── ProductRepository.java # Derived-query JPA methods
│   │   │   │       │       ├── CategoryRepository.java
│   │   │   │       │       └── SupplierRepository.java
│   │   │   │       ├── dao/
│   │   │   │       │   └── ProductDAO.java            # Repository wrapper (add, update, soft/hard delete, search)
│   │   │   │       ├── service/
│   │   │   │       │   ├── ProductService.java        # Delegates to ProductDAO after validation
│   │   │   │       │   ├── InventoryService.java      # Stock increase/reduce; triggers low-stock check
│   │   │   │       │   └── validation/
│   │   │   │       │       ├── ProductValidator.java  # Validates name, SKU, price, category, supplier
│   │   │   │       │       └── InventoryValidator.java # Validates qty > 0, stock >= requested qty
│   │   │   │       └── Report/
│   │   │   │           ├── InventoryReportService.java # Detects low-stock; formats & sends alert email
│   │   │   │           └── EmailService.java           # SMTP send via Gmail App Password
│   │   │   └── resources/
│   │   │       ├── application.properties             # DB, JPA, JWT config
│   │   │       └── stockmanagement.sql                # Legacy SQL seed file (56 sample products)
│   │   └── test/java/com/inventory/
│   │       ├── ControllerTest.java
│   │       ├── InventoryServiceTest.java
│   │       ├── InventoryValidatorTest.java
│   │       ├── ProductDAOTest.java
│   │       ├── ProductEntityTest.java
│   │       ├── ProductServiceTest.java
│   │       ├── ProductValidatorTest.java
│   │       ├── ServerApplicationTests.java
│   │       └── security/
│   │           ├── AuthControllerTest.java
│   │           ├── AuthServiceTest.java
│   │           ├── AuthUtilTest.java
│   │           ├── CustomUserDetailsServiceTest.java
│   │           └── JwtAuthenticationFilterTest.java
│   └── pom.xml
│
└── frontend/                         # React application
    ├── public/
    │   └── index.html
    ├── src/
    │   ├── App.js                    # Router, context providers, Protected route wrapper
    │   ├── pages/
    │   │   ├── LoginPage.jsx         # Split-panel login with theme toggle
    │   │   ├── RegisterPage.jsx      # Registration form with role selector
    │   │   ├── DashboardPage.jsx     # Summary cards, category chart, low-stock list
    │   │   ├── ProductsPage.jsx      # Searchable product table with edit/delete
    │   │   ├── AddProductPage.jsx    # Add-product form (SUPPLIER+)
    │   │   ├── ReportsPage.jsx       # 4-tab analytics: Summary/Low Stock/Value/Categories
    │   │   └── AddUserPage.jsx       # Create user form (ADMIN only)
    │   ├── components/
    │   │   ├── Navbar.jsx            # Fixed top bar: title, theme toggle, notifications, user badge
    │   │   ├── Sidebar.jsx           # Fixed left nav with role-filtered links
    │   │   ├── ProductForm.jsx       # Reusable add/edit form with client-side validation
    │   │   ├── ProductTable.jsx      # Data table with status badges and action buttons
    │   │   ├── ChartComponent.jsx    # Canvas bar chart (no external library)
    │   │   ├── SummaryCard.jsx       # Animated KPI card with hover lift effect
    │   │   └── LowStockBadge.jsx     # "LOW STOCK" / "OK" inline status badge
    │   ├── context/
    │   │   ├── AuthContext.js        # Login/logout state, role hierarchy, token persistence
    │   │   ├── InventoryContext.jsx  # Global product state (fetch, add, update, delete)
    │   │   ├── ThemeContext.js       # dark/light toggle, persisted to localStorage
    │   │   └── ToastContext.jsx      # Stacked toast notifications (success/error/warning/info)
    │   ├── hooks/
    │   │   ├── useAuth.js            # Thin wrapper around AuthContext
    │   │   ├── useProducts.js        # Fetch, normalise, add, edit, delete; lowStockProducts memo
    │   │   └── useReports.js         # Report fetching hook (currently unused)
    │   ├── services/
    │   │   ├── AuthService.js        # register / login / logout helpers
    │   │   ├── ProductService.js     # REST calls for products
    │   │   └── UserService.js        # REST calls for /api/users (ADMIN feature)
    │   ├── utilities/
    │   │   ├── ApiUtils.js           # Axios instance with JWT interceptor
    │   │   ├── Constants.js          # API_BASE_URL, ROLES, TOKEN_KEY, CATEGORIES
    │   │   ├── StorageUtils.js       # localStorage helpers for token and user object
    │   │   ├── ValidationUtils.js    # validateProduct / validateLogin / validateRegister
    │   │   └── FormatUtils.js        # Indian-locale currency (INR), number formatting, truncate
    │   └── styles/
    │       ├── global.css            # CSS variables (dark/light themes), base styles, buttons, cards
    │       ├── dashboard.css         # Dashboard grid, summary cards, chart panels
    │       ├── inventory.css         # Table, form, toolbar, badge styles
    │       └── login.css             # Split-panel login layout
    └── package.json
```

---

## Architecture Overview

```
Browser (React SPA)
        |
        |  HTTP + Authorization: Bearer <JWT>
        v
+-----------------------------------+
|         Spring Boot API           |
|                                   |
|  CorsConfig                       |  <- allows localhost:3000
|  JwtAuthenticationFilter          |  <- validates token on every request
|  WebSecurityConfig                |  <- route-level permission rules
|                                   |
|  AuthController  /auth/**         |  <- public: register, login
|  Controller      /api/**          |  <- authenticated: products, stock, report
|                                   |
|  AuthService --> UserRepository   |
|  ProductService -> ProductDAO --> ProductRepository
|  InventoryService -> ProductDAO   |
|  InventoryReportService           |  <- reads products, sends email
|  EmailService (Gmail SMTP)        |
+----------------+------------------+
                 |  JPA / Hibernate
                 v
          MySQL Database
    +----------------------+
    |  users               |
    |  products            |
    |  categories          |
    |  supplier            |
    |  transactions        |
    +----------------------+
```

---

## Database Schema

Tables are created and updated automatically by Hibernate (`ddl-auto=update`). No manual SQL import is required for the application to run.

### `users`
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | Auto-increment |
| username | VARCHAR(100) UNIQUE | Login identifier |
| password | VARCHAR(255) | BCrypt hash |
| email | VARCHAR(150) UNIQUE | |
| role | ENUM | `ADMIN`, `SUPPLIER`, `CUSTOMER` |
| active | BOOLEAN | Default `true` |
| created_at | DATETIME | Set by `@PrePersist` |

### `products`
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | Auto-increment |
| name | VARCHAR(200) | Indexed |
| description | VARCHAR(1000) | Optional |
| sku | VARCHAR(50) UNIQUE | Stock-keeping unit |
| price | DECIMAL(12,2) | Must be > 0 |
| quantity | INT | Current stock level |
| reorder_level | INT | Default 10; low-stock threshold |
| is_active | BOOLEAN | Soft-delete flag (default `true`) |
| created_at | DATETIME | Immutable, set on insert |
| updated_at | DATETIME | Updated by `@PreUpdate` |
| category_id | BIGINT FK | References `categories` |
| supplier_id | BIGINT FK | References `supplier` |

### `categories`
| Column | Type |
|---|---|
| id | BIGINT PK |
| name | VARCHAR(100) UNIQUE |
| description | VARCHAR(500) |

**Auto-seeded on first start:** Electronics (id=1), Stationery (id=2), Furniture (id=3), Food (id=4)

### `supplier`
| Column | Type |
|---|---|
| id | BIGINT PK |
| name | VARCHAR(200) |
| contact_email | VARCHAR(200) |
| phone | VARCHAR(20) |

**Auto-seeded on first start:** Default Supplier (id=1), Global Traders (id=2)

### `transactions`
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | |
| product_id | BIGINT FK | References `products` |
| type | VARCHAR | `"INCREASE"` or `"REDUCE"` |
| quantity | INT | Amount moved |
| created_at | DATETIME | Immutable |

> The `stockmanagement.sql` file in `src/main/resources/` is a legacy seed dump with 56 sample products from the original flat-table schema. It is **not auto-imported** — the JPA schema is the authoritative source. You may run it manually if you want sample data, but you must first create matching categories and suppliers since the current schema uses foreign keys.

---

## API Reference

All `/api/**` endpoints require a valid `Authorization: Bearer <token>` header.
`/auth/**` endpoints are public.

### Authentication

| Method | Endpoint | Body | Response |
|---|---|---|---|
| POST | `/auth/register` | `{ username, password, email, role }` | `{ token, username, role }` |
| POST | `/auth/login` | `{ username, password }` | `{ token, username, role }` |

`role` must be one of: `ADMIN`, `SUPPLIER`, `CUSTOMER`

### Products

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/products` | All products (including inactive) |
| GET | `/api/products/active` | Active products only |
| GET | `/api/product/{id}` | Single product by ID |
| GET | `/api/products/search?name=` | Case-insensitive partial name search |
| GET | `/api/products/category/{categoryId}` | Products filtered by category |
| GET | `/api/products/supplier/{supplierId}` | Products filtered by supplier |
| POST | `/api/product` | Add a new product |
| DELETE | `/api/product/{id}` | Hard delete (permanent) |
| PATCH | `/api/product/{id}/deactivate` | Soft delete — sets `active = false` |

**POST `/api/product` request body:**
```json
{
  "name": "Wireless Mouse",
  "sku": "WM-001",
  "description": "Ergonomic wireless mouse",
  "price": 1200.00,
  "quantity": 50,
  "reorderLevel": 10,
  "category": { "id": 1 },
  "supplier": { "id": 1 }
}
```

### Stock Control

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/increase/{id}/{qty}` | Add `qty` units to a product's stock |
| POST | `/api/reduce/{id}/{qty}` | Remove `qty` units from a product's stock |

Reducing stock automatically triggers `InventoryReportService.checkAndSendLowStockAlert()`.
If `qty` exceeds current stock, the request fails with a descriptive error.

### Reports

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/report` | Run low-stock check and send email alert if needed |

Returns `200 OK` with body `"Report generated - check your email"`.

---

## Security & Roles

### JWT Flow

1. Client calls `POST /auth/login` and receives a signed JWT
2. Token payload contains: `sub` (username), `role` (e.g. `"ADMIN"`), `iat`, `exp`
3. Frontend stores the token in `localStorage` under key `inv_token`
4. Every API request includes `Authorization: Bearer <token>`
5. `JwtAuthenticationFilter` validates the signature and expiry, then sets `SecurityContextHolder`
6. Token expires after **24 hours** (`jwt.expiration-ms=86400000`)

### Backend Route Permissions

| Path pattern | Access |
|---|---|
| `/auth/**` | Public (no token required) |
| `/public/**` | Public |
| `/api/**` | Any authenticated user |
| `/admin/**` | `ADMIN` only |
| `/supplier/**` | `SUPPLIER` or `ADMIN` |
| `/customer/**` | `CUSTOMER` or `ADMIN` |

> As currently configured, all `/api/**` routes accept any authenticated user regardless of role. Role enforcement for write operations is handled on the frontend by conditionally showing/hiding buttons. To enforce roles server-side, add `@PreAuthorize` annotations to controller methods — `@EnableMethodSecurity` is already active on `WebSecurityConfig`.

### Frontend Role Hierarchy

Roles are mapped to a numeric level: `ADMIN(3) ≥ SUPPLIER(2) ≥ CUSTOMER(1)`.

| Feature | CUSTOMER | SUPPLIER | ADMIN |
|---|---|---|---|
| View dashboard, products, reports | Yes | Yes | Yes |
| Add / edit products | No | Yes | Yes |
| Delete products | No | No | Yes |
| Add users | No | No | Yes |

---

## Getting Started

### Prerequisites

- **Java 21** (or Java 23 — see [Known Issues](#known-issues--notes))
- **Maven 3.8+** (or use the included `mvnw` wrapper)
- **Node.js 18+** and **npm**
- **MySQL 8.x**
- A **Gmail account** with 2FA enabled, for generating an App Password (required for email alerts)

---

### 1. Database Setup

```sql
-- Run in MySQL Workbench or the MySQL CLI:
CREATE DATABASE stockmanagement CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Hibernate creates all tables automatically on first run. No schema import is needed.

---

### 2. Email Configuration

Low-stock alerts are sent via Gmail SMTP. You need a **Gmail App Password** — not your regular password.

**Generate an App Password:**
1. Go to [myaccount.google.com/security](https://myaccount.google.com/security)
2. Enable **2-Step Verification** if not already done
3. Search for **App passwords**, select "Mail" and "Other (custom name)" → Generate
4. Copy the 16-character password shown

**Edit `backend/src/main/java/com/inventory/Report/EmailService.java`:**
```java
private final String username = "your.email@gmail.com";   // sender Gmail address
private final String password = "abcd efgh ijkl mnop";    // your 16-char App Password
```

**Set the recipient in `InventoryReportService.java`:**
```java
emailService.sendEmail(
    "recipient@example.com",   // <-- add your email here
    subject,
    body.toString()
);
```

---

### 3. Backend Setup

```bash
cd backend

# Using the Maven wrapper (no Maven install needed):
./mvnw spring-boot:run

# Or with system Maven:
mvn spring-boot:run
```

The API starts on **http://localhost:8080**.

On first startup, you should see in the console:
```
Categories count: 4
Suppliers count: 2
```
This confirms seed data was inserted. You can now use category IDs 1–4 and supplier IDs 1–2 when adding products.

**Override credentials without editing files:**
```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/stockmanagement
export SPRING_DATASOURCE_USERNAME=your_user
export SPRING_DATASOURCE_PASSWORD=your_password
./mvnw spring-boot:run
```

---

### 4. Frontend Setup

```bash
cd frontend
npm install
npm start
```

The app opens at **http://localhost:3000**.

**First login:** Go to `/register`, create an account, and select a role. Use `ADMIN` for full access during development.

To change the backend URL, edit `src/utilities/Constants.js`:
```js
export const API_BASE_URL = 'http://localhost:8080';
```

---

## Configuration Reference

### `backend/src/main/resources/application.properties`

```properties
# MySQL connection
spring.datasource.url=jdbc:mysql://localhost:3306/stockmanagement
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate
spring.jpa.hibernate.ddl-auto=update   # change to 'validate' in production
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT secret (Base64-encoded 256-bit key)
# To generate your own: echo -n "your-32-character-secret-here!!!" | base64
jwt.secret=dGhpcy1pcy1hLXNlY3JldC1rZXktZm9yLWludmVudG9yeS1zeXN0ZW0tMjAyNg==
jwt.expiration-ms=86400000   # 24 hours
```

> **Security reminder:** Replace the default `jwt.secret` before any deployment. The value above is a known public default and must never be used in production.

---

## Running Tests

The backend has 13 test classes covering controllers, services, validators, DAO, and security.

```bash
cd backend

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=AuthServiceTest

# Run with console output (no surefire report files)
./mvnw test -Dsurefire.useFile=false
```

| Test class | What it covers |
|---|---|
| `ControllerTest` | MockMvc for all `/api/**` product, stock, and report endpoints |
| `AuthControllerTest` | Register and login endpoint responses |
| `AuthServiceTest` | Register/login logic, duplicate username/email detection |
| `AuthUtilTest` | JWT generation, claim extraction, validation, and expiry |
| `JwtAuthenticationFilterTest` | Filter with valid, invalid, and missing tokens |
| `CustomUserDetailsServiceTest` | User loading and not-found case |
| `ProductServiceTest` | Service delegation and validator interaction |
| `ProductDAOTest` | DAO operations with mocked repository |
| `ProductEntityTest` | `isLowStock()` business logic and entity fields |
| `ProductValidatorTest` | All validation branches (null, blank, negative price, etc.) |
| `InventoryServiceTest` | Reduce/increase stock including insufficient-stock edge case |
| `InventoryValidatorTest` | Quantity validation and stock availability checks |
| `ServerApplicationTests` | Spring context loads successfully |

---

## Known Issues & Notes

**Java 23 + Mockito incompatibility.** Byte Buddy 1.14.13 (bundled with Spring Boot 3.2.5) only supports up to Java 22. Running tests on Java 23 throws `MockitoException: Java 23 (67) is not supported`. Fix by either using **Java 21** or upgrading the Spring Boot version in `pom.xml`:
```xml
<version>3.3.5</version>
```

**`/api/**` routes are open to all authenticated users.** The `WebSecurityConfig` currently uses `.requestMatchers("/api/**").permitAll()` (effectively requiring only a valid token, not a specific role). Role-based restrictions on write operations are enforced on the frontend only. To harden the backend, add `@PreAuthorize` annotations to controller methods.

**CORS is limited to `http://localhost:3000`.** If your frontend runs on a different origin, update `CorsConfig.java`:
```java
config.setAllowedOrigins(List.of("http://localhost:YOUR_PORT"));
```

**Email recipient is empty by default.** `InventoryReportService.java` passes `""` as the recipient address. Set a real email address before using the report endpoint.

**`DatabaseConnection.java`** in `com.database` is a legacy JDBC connection class from the original version of the project. It is not used anywhere in the current JPA-based implementation and can be safely deleted.

**`useReports.js`** fetches `/api/reports/:type`, which is not a real backend endpoint. All report data shown in `ReportsPage.jsx` is computed client-side from the product list. The hook is not called anywhere in the current codebase.

**The `stockmanagement.sql` file** uses the old flat schema (single `products` table, no foreign keys, no `sku` or `is_active` columns). It is incompatible with the current entity model and is kept for reference only. Do not import it into the current database.

**Category and Supplier IDs in the Add Product form are fixed to 1–4.** `ProductForm.jsx` renders a hardcoded `[1, 2, 3, 4]` dropdown for both `categoryId` and `supplierId`. These must correspond to existing rows in the database. The `StarterDataConfig` seeds exactly four categories (IDs 1–4) and two suppliers (IDs 1–2), so valid values for `supplierId` are only 1 or 2. A future improvement would be to fetch these lists dynamically from the API.
