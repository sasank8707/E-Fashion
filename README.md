# E-Fashion Website (2025)

Developed a web-based e-commerce platform using HTML and CSS for the frontend with features like product browsing and user interface design. Integrated backend and database support for managing products and user data. Gained hands-on experience in web development and system design.

---

## Features

- **Product Browsing & Catalog**: View a list of fashion items filtered by category (T-shirts, Jeans, Dresses, etc.) with pagination-ready views.
- **Product Details**: Access in-depth information about products, including real-time stock levels, pricing, descriptions, and high-quality images.
- **Secure Authentication**: Register and login functionalities powered by secure backend password verification using **BCrypt** hashing.
- **Persistent Shopping Cart**: Add products, modify quantities with validation against available stock, and remove items.
- **Checkout & Order Placement**: Input shipping details to place order records stored securely in the MySQL database.
- **Responsive Web UI**: Built using standard CSS grid/flexbox and Bootstrap 5 to guarantee accessibility on desktop, tablet, and mobile browsers.

---

## Technologies Used

- **Frontend**: HTML5, CSS3, Vanilla JavaScript (ES6+), Bootstrap 5 (for layouts and icons).
- **Backend**: Java 17, Spring Boot 3.2.5, Spring Data JPA (Hibernate).
- **Database**: MySQL 8.0.
- **Security**: Spring Security Crypto (BCrypt password hashing).
- **Build Tool**: Maven.

---

## Architecture Diagram

```
[ Frontend (HTML/CSS/JS) ]
          │ (REST API / Fetch JSON)
          ▼
[ Backend (Spring Boot Rest Controllers) ]
          │ (Spring Data JPA / Hibernate)
          ▼
[ Database (MySQL Relational Schema) ]
```

---

## Relational Database Schema

The database consists of 6 tables mapping fashion data and operations:
1. `users`: Stores user credentials. Passwords are saved as BCrypt hashes.
2. `categories`: Stores distinct catalog divisions (e.g. T-Shirts, Shoes).
3. `products`: Reference details, price, current stock count, and associated category.
4. `cart_items`: Stores user cart products and quantities.
5. `orders`: Tracks user orders, total checkout cost, dates, shipping addresses, and status.
6. `order_items`: Maps a specific order to the ordered items, capturing purchase quantity and price.

---

## Local Setup Instructions

### 1. Requirements
Ensure you have the following installed:
- **Java JDK 17** or higher.
- **MySQL Server** (version 8.0+).
- **An IDE** (IntelliJ IDEA Community, Eclipse, or VS Code) or **Maven** installed.

### 2. Database Setup
1. Open your MySQL Command Line Client or tool (Workbench, phpMyAdmin).
2. Create the database and seed the tables by running the script located at `database/schema.sql`:
   ```sql
   SOURCE c:/Users/sasan/Desktop/E-Fashion/database/schema.sql;
   ```
   *(Ensure you replace the path with your actual local path if it differs)*

### 3. Backend Configuration
1. Open the backend configuration file at `backend/src/main/resources/application.properties`.
2. Update the datasource configuration with your MySQL username and password:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

### 4. Running the Backend
- **Via IDE**: Import the `backend/` folder as a Maven Project in IntelliJ IDEA or Eclipse. Run `EcomApplication.java` as a Java Application.
- **Via Terminal** (if Maven is in your PATH):
  ```bash
  cd backend
  mvn spring-boot:run
  ```
  The API will start listening at `http://localhost:8080`.

### 5. Running the Frontend
1. The frontend comprises static files. Simply navigate to the `frontend/` directory.
2. Double-click `index.html` to open it in your web browser, or serve it using any local server extension (e.g., Live Server in VS Code).
3. Register a new user account, login, add products to your cart, and place an order.

---

## API Documentation

All request and response payloads use JSON.

### Authentication Endpoints

#### Register User
- **Method**: `POST`
- **Endpoint**: `/api/users/register`
- **Request Body**:
  ```json
  {
    "name": "Jane Doe",
    "email": "jane@example.com",
    "password": "securepassword123"
  }
  ```
- **Response Example (201 Created)**:
  ```json
  {
    "id": 3,
    "name": "Jane Doe",
    "email": "jane@example.com",
    "role": "USER"
  }
  ```

#### Login User
- **Method**: `POST`
- **Endpoint**: `/api/users/login`
- **Request Body**:
  ```json
  {
    "email": "jane@example.com",
    "password": "securepassword123"
  }
  ```
- **Response Example (200 OK)**:
  ```json
  {
    "id": 3,
    "name": "Jane Doe",
    "email": "jane@example.com",
    "role": "USER"
  }
  ```

### Product Endpoints

#### Get All Products
- **Method**: `GET`
- **Endpoint**: `/api/products` (Optional query parameter: `?categoryId=1`)
- **Response Example (200 OK)**:
  ```json
  [
    {
      "id": 1,
      "name": "Classic White Tee",
      "description": "100% premium cotton crew neck t-shirt.",
      "price": 19.99,
      "stockQuantity": 50,
      "category": {
        "id": 1,
        "name": "T-Shirts"
      },
      "imageUrl": "https://images.unsplash.com..."
    }
  ]
  ```

#### Get Product by ID
- **Method**: `GET`
- **Endpoint**: `/api/products/{id}`
- **Response Example (200 OK)**:
  ```json
  {
    "id": 1,
    "name": "Classic White Tee",
    "description": "100% premium cotton crew neck t-shirt.",
    "price": 19.99,
    "stockQuantity": 50,
    "category": {
      "id": 1,
      "name": "T-Shirts"
    },
    "imageUrl": "https://images.unsplash.com..."
  }
  ```

### Shopping Cart Endpoints

#### View User's Cart
- **Method**: `GET`
- **Endpoint**: `/api/cart/{userId}`
- **Response Example (200 OK)**:
  ```json
  [
    {
      "id": 1,
      "user": { "id": 2, "name": "John Doe", "email": "john@gmail.com", "role": "USER" },
      "product": { "id": 1, "name": "Classic White Tee", "price": 19.99, "stockQuantity": 49 },
      "quantity": 2
    }
  ]
  ```

#### Add Product to Cart
- **Method**: `POST`
- **Endpoint**: `/api/cart/{userId}/add`
- **Request Body**:
  ```json
  {
    "productId": 1,
    "quantity": 1
  }
  ```
- **Response Example (201 Created)**:
  ```json
  {
    "id": 1,
    "quantity": 3
  }
  ```

### Checkout & Order Endpoints

#### Place Order
- **Method**: `POST`
- **Endpoint**: `/api/orders/place/{userId}`
- **Request Body**:
  ```json
  {
    "shippingAddress": "123 Fashion Ave, NY 10001"
  }
  ```
- **Response Example (201 Created)**:
  ```json
  {
    "id": 1,
    "orderDate": "2026-08-17T23:55:00",
    "totalAmount": 59.97,
    "status": "PLACED",
    "shippingAddress": "123 Fashion Ave, NY 10001"
  }
  ```

---

## Future Scope / Improvements

- **JWT Authentication**: Transition session details to secure JSON Web Tokens.
- **Admin Management Portal**: Build UI layouts for administrators to create, read, update, and delete catalog items.
- **Search and Pagination**: Add full-text matching search filters and paginated product fetches.
- **Order Tracking**: Provide shipping updates and user-facing delivery milestones.
