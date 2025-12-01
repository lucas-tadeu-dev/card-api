# Card API – Hyperativa Challenge

This project was developed as part of the Hyperativa backend challenge.  
It provides an API for securely storing and verifying full credit card numbers, including JWT authentication, TXT import, logging, and MySQL persistence.

---

## 🚀 Technologies
- Java 17  
- Spring Boot 3  
- Spring Security (JWT)  
- Spring Data JPA  
- MySQL  
- Maven  
- JUnit + Mockito  
- Lombok  

---

#  Setup Instructions

## 1. Clone the repository
git clone https://github.com/your-user/card-api.git
cd card-api

## 2. Create database
Create the MySQL database
CREATE DATABASE card_api;

## 3. Configure the application

Edit src/main/resources/application-dev.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/card_api?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update

jwt.secret=YOUR_64_BYTE_SECRET
jwt.expiration-in-ms=3600000


Generate a 64-byte key here:
https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx

Build and run the project
Build
mvn clean install

Run
mvn spring-boot:run


API will start at:

http://localhost:8080

## 4. Authentication

All protected endpoints require JWT authentication.

Login Endpoint
POST /api/auth/login
Request
{
  "username": "admin",
  "password": "admin123"
}

Response
{
  "token": "eyJhbGciOiJIUzI1NiIs..."
}


## 5. Use the token in every protected request:

Authorization: Bearer <TOKEN>

Card Endpoints
3.1. Insert a Single Card
POST /api/cards
Request
{
  "cardNumber": "4111111111111111"
}

Response
{
  "id": 1,
  "maskedCard": "************1111"
}

## 6. Import Cards from TXT
POST /api/cards/upload

Upload a TXT file with one card number per line.

Example cards.txt:

4111111111111111
5500000000000004

Using curl
curl -X POST http://localhost:8080/api/cards/upload \
  -H "Authorization: Bearer <TOKEN>" \
  -F "file=@cards.txt"

Response
{
  "imported": 2
}

## 7.Check if a Card Exists
GET /api/cards/check?cardNumber=4111111111111111
Response if card exists
{
  "exists": true,
  "cardId": 1
}

If card does NOT exist
{
  "exists": false
}

Security & Data Protection

Full credit card numbers are never stored in plain text.

A secure SHA-256 hash is stored instead.

Only masked card numbers are returned.

JWT authentication protects all operations.

All incoming requests are logged with:

HTTP method

Path

Status code

Execution time

Authenticated user (if applicable)

Running Unit Tests
mvn test


## 8. Tests cover:

JWT provider

Authentication

Card hashing

Card service

TXT import

Controllers

Logging filter

Application startup

## 9. Project Structure
src/main/java/com/hyperativa/cardapi/
│
├── auth/
│   ├── jwt/
│   ├── domain/
│   ├── web/
│
├── card/
│   ├── domain/
│   ├── service/
│   ├── web/
│
├── logging/
│
└── config/