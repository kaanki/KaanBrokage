# KaanBrokage – Stock Order Management System

A Spring Boot application that simulates a basic stock market order management system. Users (customers) can manage their assets and place buy/sell orders. Admin can match pending orders and update asset balances.

---

## Table of Contents
1. [Project Overview](#project-overview)  
2. [Business Domain](#business-domain)  
3. [Features](#features)  
4. [Tech Stack](#tech-stack)  
5. [Getting Started](#getting-started)  
6. [Database](#database)  
7. [API Endpoints](#api-endpoints)  
8. [Testing](#testing)  
9. [Future Improvements](#future-improvements)  

---

## Project Overview
This project is designed as a **spring-boot application** with H2 in-memory database. It supports:
- Managing customers and assets
- Creating buy/sell orders
- Admin-level order matching
- REST API endpoints secured per customer  

---

## Business Domain
- Customers have **accounts with assets**, e.g., TRY, THYAO, GARAN.  
- Customers can place **buy or sell orders** for assets.  
- Orders can be **pending, matched or canceled**.  
- Admin can **match pending orders**, updating both the asset balances and order statuses.

---

## Features
- Add / view / manage assets  
- Place buy/sell orders  
- List and filter orders  
- Admin endpoint to match orders  
- Validation for all inputs
- Authorization for ADMIN and CUSTOMERS
- Unit tests for all endpoints  

---

## Tech Stack
- Java 17+  
- Spring Boot 3.5.5 
- Spring Web, Spring Data JPA  
- H2 in-memory database  
- Maven  
- JUnit 5 + MockMvc (testing)  

---

## Getting Started
### Prerequisites
- Java 17+ installed  
- Maven 3.11 installed  

### Build and Run
```bash
# Clone the repo
git clone <repository_url>
cd KaanBrokage

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
