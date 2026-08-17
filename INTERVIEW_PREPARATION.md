# Interview Preparation Guide - E-Fashion Website

This document is designed to help you explain the **E-Fashion Website** project in a technical interview (such as for Cognizant, Infosys, Wipro, TCS, or other software engineering roles). It provides clear, student-level explanations of the technical architecture, code design decisions, flows, and contains **35 realistic interview questions and answers** based on the actual implementation.

---

## 1. Project Overview & Design Decisions

### What problem does it solve?
It provides a functional, responsive, and easy-to-use digital catalog where customers can register an account, browse fashion items dynamically by categories, check stock availability, add products to their shopping cart, and place orders. It acts as an integration benchmark connecting static frontend interfaces to Java Spring Boot REST controllers and a relational MySQL backend.

### Why this technology stack?
- **HTML5 & CSS3**: Core foundation for structuring and styling the web. Gives full design flexibility.
- **Vanilla JavaScript**: Lightweight and standard. Handles API queries (`fetch`), handles frontend state management (`localStorage`), and modifies the DOM dynamically. It demonstrates raw DOM scripting skills without relying on complex, interview-heavy frameworks like React or Angular.
- **Bootstrap 5**: Utilized minimally to ensure responsive grids, neat navigation elements, form styling, and interactive layouts across mobile, tablet, and desktop screens.
- **Spring Boot**: The leading Java framework for web application development. It simplifies configuration, dependency management via Maven, database interaction via JPA, and makes building REST APIs rapid and standardized.
- **Spring Data JPA / Hibernate**: An Object-Relational Mapping (ORM) layer that eliminates standard boilerplate SQL statements. Entities map directly to MySQL tables, and repository interfaces handle database CRUD operations automatically.
- **MySQL**: A highly reliable, open-source relational database management system (RDBMS) standard in industry and student projects. Perfect for handling relational entities (Users, Products, Orders).
- **BCrypt**: A cryptographically secure, standard password hashing algorithm. It prevents plain-text password breaches in databases by salting and hashing credentials securely.

---

## 2. System Architecture & Flows

```
[ Frontend (HTML/CSS/JS) ] ──(REST API Requests)──► [ Controller ] ──► [ Service ] ──► [ Repository ] ──► [ MySQL DB ]
```

### The Controller → Service → Repository Flow
1. **Controller Layer**: Exposes REST endpoints (e.g. `/api/users/login`). It accepts incoming client JSON payloads, maps them to Java models, and passes inputs to the Service layer. It returns clean HTTP status codes (`200 OK`, `201 Created`, `400 Bad Request`, `401 Unauthorized`).
2. **Service Layer**: House of business logic and validation. For instance, the `UserService` hashes passwords with BCrypt on registration, validates email structures, checks item stock levels before saving a cart item, and adjusts inventory when an order is placed.
3. **Repository Layer**: Extends `JpaRepository`. Translates repository methods (like `findByEmail` or `save`) into database SQL queries.

### The Authentication Flow
1. **Registration**: User sends Name, Email, and Password. The backend checks if the email exists. The password is encrypted with `BCryptPasswordEncoder` and stored in the database.
2. **Login**: User inputs Email and Password. The backend retrieves the User entity by email. It runs `passwordEncoder.matches(rawPassword, encodedPassword)`. If true, user details (excluding password) are returned; the client saves these basic details in `localStorage` to keep UI state persistent (e.g. "Hello, John").
3. **Security**: Passwords are never returned in JSON outputs or written in `localStorage`.

### The Product Flow
1. Client loads `products.html`. A Javascript function sends a `GET /api/products` call.
2. The controller queries the `ProductService`, which fetches records from the `ProductRepository`.
3. The repository runs a SQL query on the `products` table in MySQL (joining categories if filtered).
4. Results are returned as a JSON array to JS, which updates the DOM dynamically to render product cards.

### The Order Flow
1. User clicks "Place Order" in `checkout.html` and submits a shipping address.
2. Javascript sends a `POST /api/orders/place/{userId}` request.
3. The backend retrieves all active `cart_items` for the user.
4. For each item, it checks stock: `if (product.getStockQuantity() < item.getQuantity())` -> Throws Exception.
5. If in stock, it deducts the item quantity from the product's database inventory.
6. It creates an `Order` and associated `OrderItem` list.
7. Saves the order records to MySQL, deletes the user's `cart_items`, and returns the order reference.

---

## 3. Database Schema Overview

- **categories**: `id` (PK), `name` (unique).
- **products**: `id` (PK), `name`, `description`, `price`, `stock_quantity`, `category_id` (FK referencing `categories.id`), `image_url`.
- **users**: `id` (PK), `name`, `email` (unique), `password` (BCrypt), `role`.
- **cart_items**: `id` (PK), `user_id` (FK referencing `users.id`), `product_id` (FK referencing `products.id`), `quantity`.
- **orders**: `id` (PK), `user_id` (FK referencing `users.id`), `order_date`, `total_amount`, `status`, `shipping_address`.
- **order_items**: `id` (PK), `order_id` (FK referencing `orders.id`), `product_id` (FK referencing `products.id`), `quantity`, `price`.

---

## 4. 35 Interview Questions & Answers

### General & Project Concept
#### 1. Explain your project in simple words.
**Answer:** The E-Fashion Website is a web-based e-commerce platform designed to let users register, log in, browse fashion items by categories, view product details, add items to a persistent shopping cart, and place orders. The frontend is built using HTML, CSS, Bootstrap, and Vanilla JavaScript, which connects via REST APIs to a Spring Boot backend integrated with a MySQL database.

#### 2. What was your contribution to this project?
**Answer:** I designed the system end-to-end. I created the MySQL relational database schema, implemented the complete Spring Boot backend structure (controllers, services, repositories, validation, and BCrypt security configuration), and built the responsive frontend UI pages from scratch using standard CSS and JavaScript.

#### 3. Why did you choose an e-commerce website for your project?
**Answer:** E-commerce systems are excellent for demonstrating core full-stack engineering skills. They require database design (managing users, products, orders), CRUD operations, request validation, state management, security considerations (like password hashing), and asynchronous frontend-backend communication.

#### 4. How did you structure your project files?
**Answer:** I kept the project separated into three main layers: `database/` (contains schema scripts), `frontend/` (contains static HTML, custom CSS, and app JS files), and `backend/` (contains the Spring Boot Maven project structured in standard Java packages: controller, service, repository, entity).

#### 5. What are the key technical challenges you faced in this project?
**Answer:** One challenge was managing product inventory concurrently during checkout. If two users checked out the last item at the same time, it could create negative stock. I solved this in the backend service layer by checking available quantity immediately before placing the order and throwing an exception if the stock was insufficient.

---

### Backend (Java & Spring Boot)
#### 6. Why did you choose Spring Boot instead of plain Java Servlets?
**Answer:** Spring Boot provides a pre-configured starter project model. It embeds Tomcat automatically, manages dependency injection, and integrates cleanly with Spring Data JPA. This saves us from writing boilerplate servlet mapping code and manual database connection handling.

#### 7. What is the role of `EcomApplication.java` in your backend?
**Answer:** It is the entry point of the Spring Boot application, containing the main method that invokes `SpringApplication.run(...)`. I also defined beans for the password encoder (`BCryptPasswordEncoder`) and the global CORS configuration inside it.

#### 8. How does Spring Boot map JSON requests to Java Objects?
**Answer:** By using the `@RequestBody` annotation in the Controller parameters, Spring Boot utilizes the Jackson library internally to deserialize incoming JSON payloads directly into Java Objects or Maps.

#### 9. What is a REST API?
**Answer:** REST (Representational State Transfer) is an architectural style for designing APIs. It uses standard HTTP methods (GET, POST, PUT, DELETE) to perform operations on resources identified by URIs.

#### 10. Which HTTP methods did you use and why?
**Answer:** 
- `GET`: For retrieving data (e.g. `/api/products` to fetch items).
- `POST`: For creating resources (e.g. `/api/users/register`, `/api/orders/place/{userId}`).
- `PUT`: For updating resources (e.g. `/api/cart/{userId}/update/{cartItemId}` for quantity changes).
- `DELETE`: For removing resources (e.g. `/api/cart/{userId}/remove/{cartItemId}`).

#### 11. How do you handle exceptions in your controllers?
**Answer:** I wrapped critical controller logic in `try-catch` blocks. When an error is caught (like stock shortages or invalid emails), I return a custom error message mapped in JSON format alongside appropriate HTTP status codes (like `400 Bad Request` or `401 Unauthorized`).

#### 12. What is dependency injection and how is it used in your code?
**Answer:** Dependency Injection is a design pattern where the control of creating dependencies is passed to the framework container. In my project, I used the `@Autowired` annotation to inject Repositories into Services, and Services into Controllers.

#### 13. What annotations are used to define a REST Controller?
**Answer:** `@RestController` (combines `@Controller` and `@ResponseBody` to indicate that response data is written directly to the HTTP body in JSON format) and `@RequestMapping` (defines the base path mapping).

---

### Relational Database & Spring Data JPA
#### 14. Explain your database schema.
**Answer:** My database contains 6 relational tables: `users`, `categories`, `products`, `cart_items`, `orders`, and `order_items`. Primary keys uniquely identify records, and foreign keys maintain associations—for instance, `products.category_id` references `categories.id` and `cart_items.user_id` references `users.id`.

#### 15. What is Spring Data JPA?
**Answer:** It is a Spring framework module that sits on top of Hibernate (the ORM). It provides repository interfaces like `JpaRepository` that automatically generate CRUD methods without requiring us to write SQL queries.

#### 16. What is the difference between `@Entity` and `@Table` annotations?
**Answer:** `@Entity` specifies that the Java class maps to a JPA entity and its lifecycle is managed by the persistence context. `@Table` is optional and is used to customize the mapping metadata, such as setting the table name in MySQL (e.g. `@Table(name = "users")`).

#### 17. How did you implement a One-to-Many relationship in Java?
**Answer:** In `Order.java`, I defined a list of `OrderItem` objects annotated with `@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)`. This ensures that when an order is saved, all associated order items are saved automatically.

#### 18. What does `cascade = CascadeType.ALL` do?
**Answer:** It propagates database operations (persist, merge, remove) from a parent entity to its child entities. For example, deleting an `Order` will automatically cascade and delete its associated `OrderItem` records.

#### 19. What is a primary key and a foreign key?
**Answer:** A primary key is a column that uniquely identifies each row in a database table. A foreign key is a column that references the primary key of another table, establishing a relationship between them.

#### 20. Why did you choose MySQL over a NoSQL database like MongoDB?
**Answer:** E-commerce systems are highly relational. They require structured data consistency, strict relationships (a product belongs to a category, order items must belong to an order), and ACID properties (Atomicity, Consistency, Isolation, Durability) to prevent issues during transaction processing. MySQL is ideal for this.

---

### Security (BCrypt & Passwords)
#### 21. How are passwords handled in your application?
**Answer:** The frontend sends the plain password to the `/api/users/register` API. The backend service layer uses `BCryptPasswordEncoder` to generate a secure hash of the password. Only this secure hash is saved in the MySQL database. During login, the backend retrieves the hashed password and uses the encoder's matching algorithm to verify the user's password.

#### 22. Why did you use BCrypt instead of SHA-256 or MD5?
**Answer:** SHA-256 and MD5 are fast cryptographic hashing algorithms. They are vulnerable to brute-force and rainbow-table attacks. BCrypt is a slow-hashing algorithm that incorporates a salt automatically, making it extremely resistant to hardware-based dictionary attacks.

#### 23. What is a salt in password hashing?
**Answer:** A salt is a random string of characters added to a password before hashing. BCrypt generates a unique salt for every password, ensuring that if two users have the same password, their resulting hashes are completely different.

#### 24. Did you store the password on the frontend?
**Answer:** No. I made sure the backend clears the password field (sets it to null) in response payloads. On the frontend, the logged-in user state is saved in `localStorage`, but it only contains safe, basic details like name, email, role, and ID. No passwords or tokens are stored.

#### 25. How did you secure your database credentials?
**Answer:** I configured `application.properties` to read database connection details (host, username, password) from environment variables using the `${VAR:default}` syntax. This allows credentials to be set dynamically on startup and avoids committing sensitive secrets to source code repositories.

---

### Frontend & API Integration
#### 26. How does the frontend communicate with the backend?
**Answer:** The frontend uses the JavaScript `fetch` API. It makes asynchronous HTTP calls to the Spring Boot REST endpoints on port 8080. The payloads are sent and received as JSON.

#### 27. What is CORS and how did you configure it?
**Answer:** CORS stands for Cross-Origin Resource Sharing. By default, browsers prevent scripts running on one origin (e.g. double-clicked frontend HTML files) from querying APIs on another origin (e.g. `http://localhost:8080`). I configured CORS in `EcomApplication.java` to allow global requests from any origin.

#### 28. What is the difference between local storage and session storage?
**Answer:** `localStorage` persists data indefinitely even after the browser tab is closed. `sessionStorage` clears data automatically when the user's session ends (e.g., closing the tab). I used `localStorage` so users remain logged in when they refresh or revisit the tab.

#### 29. How does category filtering work in your shop?
**Answer:** The catalog page `products.html` reads query parameters from the URL (e.g., `?category=2`). The JavaScript script parses this parameter and makes a conditional query to the backend API: `GET /api/products?categoryId=2`. The backend filters the results using the `findByCategoryId` repository method.

#### 30. How is responsive design achieved in your UI?
**Answer:** I used Bootstrap 5's container-row-col grid system to arrange layouts. I also wrote media queries in `css/style.css` to adjust visual sizes, product card dimensions, and layout margins on mobile screens.

---

### Operations, Cart & Checkout Flows
#### 31. Explain what happens in the database when a user checks out.
**Answer:** 
1. The backend reads the active cart items from the `cart_items` table.
2. It subtracts quantities from the `products` table stock.
3. It inserts a new row in the `orders` table (generating a new PK).
4. For each purchased item, it inserts a row in `order_items` (linked to the order PK).
5. It runs a SQL DELETE query to purge the user's records from `cart_items`.

#### 32. How do you prevent users from ordering more stock than is available?
**Answer:** In `CartService` (when updating quantities) and `OrderService` (during final checkout), the code queries the current stock quantity from the database. If the requested quantity exceeds the stock, it throws an `IllegalArgumentException` back to the controller, returning an error response to the UI.

#### 33. How does the checkout flow handle payments?
**Answer:** For this student project, payment processing is out of scope. I did not include fake payment forms or credit card fields. The checkout page collects the delivery address and records the order. This is a clean, honest approach for an academic demo.

#### 34. What will happen if you query a product with an invalid ID?
**Answer:** The `ProductService` queries the repository. If `findById` returns empty, the service throws an exception. The controller catches this exception and returns a `404 Not Found` response with a JSON payload: `{"error": "Product with ID X not found"}`.

#### 35. How would you improve this project in the future?
**Answer:** I would add a secure JWT-based stateless authentication system, implement an admin dashboard to let store staff manage products through a web interface, add search and text matching with paginated database queries, and integrate a real mock payment API like Stripe.
