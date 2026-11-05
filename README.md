# E-Fashion Online Shopping System

## 1. Project Overview

**E-Fashion Online Shopping System** is a full-stack web-based e-commerce application built for browsing, selecting, and purchasing fashion apparel and accessories online. The platform integrates a responsive frontend built with **HTML5, CSS3, Bootstrap 5, and Vanilla JavaScript (ES6+)** with a **Java 17 Spring Boot 3.2.5 RESTful backend** and a relational **MySQL 8.0** database. It supports user registration and authentication with **BCrypt** password hashing, dynamic category-based catalog browsing, real-time stock verification, persistent shopping cart management, and order placement.

---

## 2. Problem Statement

Modern online retail requires a reliable separation between client presentation, business logic validation, and persistent relational data storage. Without server-side inventory validation and persistent cart management, e-commerce applications suffer from stock inconsistencies, lost session data, and insecure credential handling.

The **E-Fashion Online Shopping System** addresses these challenges by providing:
- A decoupled client-server architecture communicating over RESTful JSON APIs.
- Secure user authentication with salted **BCrypt** password hashing rather than plain-text storage.
- Real-time inventory validation during both cart updates and order checkout to prevent over-ordering.
- A normalized 6-table relational database schema ensuring data integrity across users, categories, products, carts, and orders.

---

## 3. Key Features

- **User Registration & Authentication**: Account creation and login with email format validation, minimum password length checks, and **BCrypt** password encryption (`BCryptPasswordEncoder`).
- **Dynamic Product Catalog & Category Filtering**: Browse fashion items across 7 categories (*T-Shirts, Jeans, Shirts, Dresses, Jackets, Shoes, Accessories*) or filter products dynamically via query parameters.
- **Detailed Product Views**: View individual product descriptions, pricing, category tags, images, and real-time stock counts, with automatic disabling of out-of-stock items.
- **Persistent Database-Backed Shopping Cart**: Add items to the cart, adjust item quantities with live stock validation, remove individual items, or clear the cart.
- **Checkout & Order Processing**: Submit shipping details to convert active cart items into persistent order records, automatically deducting purchased quantities from product stock within a database transaction.
- **Responsive User Interface**: Clean, mobile-friendly layout styled with custom CSS variables (`Outfit` typography, soft-gold accent palette) and Bootstrap 5 grid components.

---

## 4. Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Frontend** | HTML5, CSS3, Vanilla JavaScript (ES6+ `fetch` API), Bootstrap 5.3, Bootstrap Icons 1.10 |
| **Backend** | Java 17, Spring Boot 3.2.5 (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`) |
| **ORM / Persistence** | Spring Data JPA, Hibernate ORM |
| **Security** | Spring Security Crypto (`BCryptPasswordEncoder`), Global CORS Configuration |
| **Database** | MySQL 8.0 (`mysql-connector-j`) |
| **Build Tool** | Apache Maven |

---

## 5. System Architecture

```text
[ Frontend Client (HTML5 / CSS3 / Bootstrap 5 / Vanilla JS) ]
                              │
                              │  HTTP / JSON REST API (Port 8080)
                              ▼
[ Spring Boot Controller Layer (User / Product / Cart / Order) ]
                              │
                              ▼
[ Spring Boot Service Layer (Validation, BCrypt, Stock & Order Logic) ]
                              │
                              ▼
[ Spring Data JPA Repository Layer (Hibernate ORM) ]
                              │
                              ▼
[ MySQL Relational Database (efashion schema) ]
```

### Frontend Architecture
The frontend (`frontend/`) is a multi-page static web application that communicates asynchronously with the Spring Boot backend via the browser `fetch` API:
- **Pages**:
  - `index.html`: Landing page featuring the hero banner, featured categories, and top 4 featured products loaded from `GET /api/products`.
  - `products.html`: Full product catalog with sidebar category filtering (`?category={id}`).
  - `product-details.html`: Detailed product view (`?id={id}`) with stock status indicator and quantity selector.
  - `cart.html`: Interactive shopping cart table supporting quantity updates (`PUT`), item removal (`DELETE`), and order subtotal calculation.
  - `checkout.html`: Shipping address form and order summary that posts to `POST /api/orders/place/{userId}` and displays the generated order reference (`#EF-{id}`).
  - `login.html` & `register.html`: Authentication forms with client-side validation and feedback alerts.
- **Shared Logic (`frontend/js/app.js`)**: Centralizes the backend API base URL (`http://localhost:8080/api`), manages user session state in `localStorage` (`efashion_user`), dynamically renders the navigation bar, and updates the live cart badge count.
- **Styling (`frontend/css/style.css`)**: Defines the design system, custom button styles, product/category card hover animations, and responsive layout adjustments.

### Backend Architecture
The backend (`backend/`) follows a layered Spring Boot architecture under the package `com.efashion.ecom`:
- **Configuration (`EcomApplication.java`)**: Main Spring Boot entry point providing `@Bean` definitions for `BCryptPasswordEncoder` and global `WebMvcConfigurer` CORS mappings.
- **Controller Layer (`controller/`)**:
  - `UserController` (`/api/users`): Handles user registration, login, and profile lookup.
  - `ProductController` (`/api/products`): Exposes CRUD endpoints and optional `categoryId` filtering for products.
  - `CartController` (`/api/cart`): Manages retrieving, adding, updating, removing, and clearing user cart items.
  - `OrderController` (`/api/orders`): Handles order placement and retrieving order history by user.
- **Service Layer (`service/`)**:
  - `UserService`: Validates user inputs, checks for duplicate emails, hashes passwords with BCrypt, and strips password hashes from response objects.
  - `ProductService`: Validates product attributes (non-empty name, non-negative price and stock) and coordinates product persistence.
  - `CartService` (`@Transactional`): Verifies user/product existence, enforces stock limits when adding or updating cart items, and prevents unauthorized cart modifications.
  - `OrderService` (`@Transactional`): Atomically validates cart contents and stock availability, deducts product inventory, calculates order totals using `BigDecimal`, persists `Order` and `OrderItem` entities, and clears the user's cart.
- **Repository Layer (`repository/`)**: Spring Data `JpaRepository` interfaces (`UserRepository`, `CategoryRepository`, `ProductRepository`, `CartItemRepository`, `OrderRepository`) providing derived query methods.
- **Entity Layer (`entity/`)**: JPA-annotated domain models (`User`, `Category`, `Product`, `CartItem`, `Order`, `OrderItem`).

---

## 6. Database Information

The MySQL database (`efashion`) is defined and seeded in `database/schema.sql` and comprises **6 relational tables**:

| Table | Primary Key | Foreign Keys & Relationships | Description |
| :--- | :--- | :--- | :--- |
| `categories` | `id` | — | Stores unique product category names (7 seeded categories). |
| `products` | `id` | `category_id` → `categories(id)` (`ON DELETE SET NULL`) | Stores product name, description, `DECIMAL(10,2)` price, `stock_quantity`, and `image_url` (14 seeded products). |
| `users` | `id` | — | Stores user name, unique email, BCrypt-hashed password, and role (`USER` or `ADMIN`). |
| `cart_items` | `id` | `user_id` → `users(id)`, `product_id` → `products(id)` (`ON DELETE CASCADE`) | Tracks active shopping cart items and quantities per user. |
| `orders` | `id` | `user_id` → `users(id)` (`ON DELETE CASCADE`) | Stores placed orders, timestamp, `total_amount`, status (`PLACED`), and `shipping_address`. |
| `order_items` | `id` | `order_id` → `orders(id)`, `product_id` → `products(id)` (`ON DELETE CASCADE`) | Stores individual line items, quantities, and unit prices for each order. |

---

## 7. Complete Project Structure

```text
E-Fashion-Online-Shopping-System/
├── .env.example                                  # Environment variable template
├── .gitignore                                    # Git ignore rules
├── INTERVIEW_PREPARATION.md                      # Technical architecture & Q&A notes
├── README.md                                     # Project documentation
├── backend/                                      # Spring Boot REST API backend
│   ├── pom.xml                                   # Maven dependencies and build configuration
│   └── src/
│       └── main/
│           ├── java/com/efashion/ecom/
│           │   ├── EcomApplication.java          # Application entry point, BCrypt & CORS beans
│           │   ├── controller/
│           │   │   ├── CartController.java       # /api/cart endpoints
│           │   │   ├── OrderController.java      # /api/orders endpoints
│           │   │   ├── ProductController.java    # /api/products endpoints
│           │   │   └── UserController.java       # /api/users endpoints
│           │   ├── entity/
│           │   │   ├── CartItem.java             # cart_items JPA entity
│           │   │   ├── Category.java             # categories JPA entity
│           │   │   ├── Order.java                # orders JPA entity
│           │   │   ├── OrderItem.java            # order_items JPA entity
│           │   │   ├── Product.java              # products JPA entity
│           │   │   └── User.java                 # users JPA entity
│           │   ├── repository/
│           │   │   ├── CartItemRepository.java   # Cart item data access
│           │   │   ├── CategoryRepository.java   # Category data access
│           │   │   ├── OrderRepository.java      # Order data access
│           │   │   ├── ProductRepository.java    # Product data access
│           │   │   └── UserRepository.java       # User data access
│           │   └── service/
│           │       ├── CartService.java          # Shopping cart business logic
│           │       ├── OrderService.java         # Checkout & order processing logic
│           │       ├── ProductService.java       # Catalog management logic
│           │       └── UserService.java          # Registration, login & BCrypt logic
│           └── resources/
│               └── application.properties        # Spring Boot & MySQL configuration
├── database/
│   └── schema.sql                                # MySQL DDL schema and initial seed data
└── frontend/                                     # Static web frontend
    ├── index.html                                # Home page & featured products
    ├── products.html                             # Full product catalog & category filters
    ├── product-details.html                      # Individual product detail view
    ├── cart.html                                 # Shopping cart management page
    ├── checkout.html                             # Order checkout & shipping page
    ├── login.html                                # User sign-in page
    ├── register.html                             # New user registration page
    ├── css/
    │   └── style.css                             # Custom stylesheet
    └── js/
        └── app.js                                # Shared API client & session utilities
```

---

## 8. Prerequisites

Ensure the following software is installed on your system before running the project:
- **Java Development Kit (JDK)**: Version **17** or higher
- **Apache Maven**: Version **3.8+** (or use the bundled Maven support in IntelliJ IDEA / Eclipse)
- **MySQL Server**: Version **8.0+**
- **Web Browser**: Any modern browser (Chrome, Edge, Firefox, Safari)

---

## 9. Installation & Setup Instructions

### Step 1: Clone the Repository
```bash
git clone https://github.com/sasank8707/E-Fashion-Online-Shopping-System.git
cd E-Fashion-Online-Shopping-System
```

### Step 2: Database Setup
1. Start your local **MySQL Server** on port `3306`.
2. Execute the `database/schema.sql` script to create the `efashion` database, tables, and seed records (7 categories, 14 products, and 2 sample accounts):
   ```bash
   mysql -u root -p < database/schema.sql
   ```
   Alternatively, open MySQL Workbench or the MySQL CLI and run:
   ```sql
   SOURCE /path/to/E-Fashion-Online-Shopping-System/database/schema.sql;
   ```

### Step 3: Environment Variable Configuration
The backend (`backend/src/main/resources/application.properties`) reads database connection settings from environment variables with sensible defaults:

```properties
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:efashion}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
```

Refer to `.env.example` for the supported environment variables:

| Variable | Default Value | Description |
| :--- | :--- | :--- |
| `DB_HOST` | `localhost` | MySQL database host |
| `DB_PORT` | `3306` | MySQL database port |
| `DB_NAME` | `efashion` | MySQL database name |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | *(empty)* | MySQL password |

Set `DB_PASSWORD` (and any other custom variables) in your terminal before starting the backend:

- **PowerShell (Windows)**:
  ```powershell
  $env:DB_USERNAME="root"
  $env:DB_PASSWORD="your_mysql_password"
  ```
- **Bash (Linux / macOS)**:
  ```bash
  export DB_USERNAME="root"
  export DB_PASSWORD="your_mysql_password"
  ```

### Step 4: Dependency Installation & Running the Backend
Navigate to the `backend` directory, install Maven dependencies, and start the Spring Boot server:

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

*(Alternatively, import the `backend/` directory as a Maven project in IntelliJ IDEA or Eclipse and run `com.efashion.ecom.EcomApplication`.)*

Once started, the backend REST API will be available at:
```text
http://localhost:8080
```

### Step 5: Running the Frontend
The frontend consists of static HTML, CSS, and JavaScript files in the `frontend/` directory:
1. Open `frontend/index.html` directly in your web browser, **or** serve the `frontend/` directory using a local static server (such as the VS Code **Live Server** extension or `python -m http.server 5500` inside `frontend/`).
2. Register a new account on `register.html` (or sign in via `login.html`), browse products on `products.html`, add items to your cart, and complete checkout on `checkout.html`.

---

## 10. REST API Reference

All endpoints accept and return JSON payloads.

### User Endpoints (`/api/users`)
| Method | Endpoint | Description | Request Body |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/users/register` | Register a new user | `{"name": "Jane Doe", "email": "jane@example.com", "password": "password123"}` |
| `POST` | `/api/users/login` | Authenticate an existing user | `{"email": "jane@example.com", "password": "password123"}` |
| `GET` | `/api/users/{id}` | Get user profile by ID | — |

### Product Endpoints (`/api/products`)
| Method | Endpoint | Description | Request Body / Query Params |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/products` | List all products (or filter by category) | Optional query param: `?categoryId={id}` |
| `GET` | `/api/products/{id}` | Get product details by ID | — |
| `POST` | `/api/products` | Create a new product | `Product` JSON object |
| `PUT` | `/api/products/{id}` | Update an existing product | `Product` JSON object |
| `DELETE` | `/api/products/{id}` | Delete a product by ID | — |

### Shopping Cart Endpoints (`/api/cart`)
| Method | Endpoint | Description | Request Body |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/cart/{userId}` | Get all cart items for a user | — |
| `POST` | `/api/cart/{userId}/add` | Add a product to the user's cart | `{"productId": 1, "quantity": 2}` |
| `PUT` | `/api/cart/{userId}/update/{cartItemId}` | Update cart item quantity | `{"quantity": 3}` |
| `DELETE` | `/api/cart/{userId}/remove/{cartItemId}` | Remove a specific item from the cart | — |
| `DELETE` | `/api/cart/{userId}/clear` | Clear all items from the user's cart | — |

### Order Endpoints (`/api/orders`)
| Method | Endpoint | Description | Request Body |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/orders/place/{userId}` | Place an order from the user's active cart | `{"shippingAddress": "123 Fashion Ave, NY 10001"}` |
| `GET` | `/api/orders/user/{userId}` | Retrieve order history for a user | — |

---

## 11. Application Screenshots

Screenshots are not currently bundled in the repository. Product catalog imagery is dynamically loaded from Unsplash URLs configured in `database/schema.sql`.

---

## 12. Future Enhancements

- **Stateless JWT Authentication**: Replace client-side session storage with signed JSON Web Tokens (JWT) and role-based Spring Security filters.
- **Admin Dashboard UI**: Build a dedicated frontend interface for `ADMIN` users to create, update, and delete products and categories and manage order statuses.
- **Search & Pagination**: Add keyword search, price range filtering, and server-side pagination (`Pageable`) for large product catalogs.
- **Order History Page**: Add a dedicated frontend view consuming `GET /api/orders/user/{userId}` so customers can track past orders.
- **Payment Gateway Integration**: Integrate a sandbox payment provider (such as Stripe or Razorpay) during checkout.

---

## 13. License

This project is developed for academic and educational demonstration purposes.
