# Trading Capture System 📈 🖥️

A full-stack trading application built on an existing production style codebase as part of a structured technical assessment delivered in partnership with UBS and Coding Black Females.

The application models a real-world trade capture workflow used at investment banks - managing trades, counterparties, books and cashflow calculations across a Java backend and a React/Typescript frontend.

## 🔧 Tech Stack

**Languages**: ![Java 17](https://img.shields.io/badge/Java-007396?style=flat-square&logo=java&logoColor=white)

**Frameworks**: ![Springboot](https://img.shields.io/badge/springboot-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![Hibernate](https://img.shields.io/badge/hibernate-59666C?style=flat-square&logo=hibernate&logoColor=white)

**Build Tool**: ![Maven](https://img.shields.io/badge/apachemaven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)

**Database**: ![H2](https://img.shields.io/badge/Database-H2-blue)

**Testing**: ![Junit5](https://img.shields.io/badge/junit5-25A162?style=flat-square&logo=junit5&logoColor=white) ![Mockito](https://img.shields.io/badge/mockito-green?style=flat-square&labelColor=green)

**Version Control**: ![Git](https://img.shields.io/badge/Git-F05032?style=flat-square&logo=git&logoColor=white) ![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)

**API Testing**: ![Postman](https://img.shields.io/badge/postman-FF6C37?style=flat-square&logo=postman&logoColor=white)

---

## 💾 Libraries

- [RSQL-JPA](https://github.com/perplexhub/rsql-jpa-specification) - RSQL filtering support for JPA repositories

---

## 📋 What I Implemented

This was a fork of a base repository. The original codebase had deliberate bugs and missing functionality. I worked through four assessed stages.

### Stage 1 - Project Setup

Configured the full-stack environment: Spring Boot backend on port 8080 (Swagger, Actuator, H2 console), React frontend on port 5173 and verified full frontend-backend communication.

### Stage 2 - Test Debugging

Ran the test suite, identified all failing tests and fixed each one. For each fix I wrote structured documentation covering:

- Problem description and symptoms
- Root cause analysis
- Solution implemented and rationale
- Verification steps

Test case documentation: `docs/test-fixes-template.md`

### Stage 3 - Feature Implementation

Built three business-critical enhancements:

#### Advanced Trade Search

- Multi-criteria search endpoint - filter by counterparty, book, trader, status and date ranges

```
/api/trades/search
```

- Paginated filtering endpoint - for high-volume result sets

```
/api/trades/filter
```

- RSQL query support - for power users using the rsql-jpa-specification plugin

```
/api/trades/rsql

example:
/api/trades/rsql?query=(counterparty.name==ABC,counterparty.name==XYZ);tradeStatus.tradeStatus==NEW
```

#### Trade Validation Engine

- Date rule valiadtion: maturity data nust follow start data, start date must follow trade date, trade date not more than 30 days in the past
- User privilege enforcement by role `(TRADER / SALES / MIDDLE_OFFICE / SUPPORT)`
- Cross-leg business rule validation: matching maturity dates, opposite pay/recieve flags, floating-leg index and fixed-leg rate requirements
- Entity status validation: user, book and counterparty must be active

#### Trade Dashboard & Blotter

- Authenticated user's personal view

```
/api/trades/dashboard/my-trades
```

- Book-level aggregation

```
/api/trades/dashboard/book/{id}/trades
```

- Portfolio summary with notional by currency and breakdown by trade type

```
/api/trades/dashboard/summary
```

- Today's trade count, notional and comparision metrics

```
/api/trades/dashboard/daily-summary
```

### Stage 4 - Bug Investigation and Fix

Diagnosed a critical cashflow calculation bug producing values approximately 100x larger than expected.

Root cause: two seperate bugs in `calculateCashflowValue()` previously in `TradeService.java`

1. Percentage formula bug - interest rate was not converted from percentage to decimal (3.5 was used instead of 0.035)
2. Precision bug - `double` was used for monetary arithmetic instead of `BigDecimal`, causing floating-point precision errors

Fix: corrected the percentage conversion and replaced `double` with `BigDecimal` throughout the calculation

Verified: $10M notional at 3.5% quarterly rate now correctly produces $87,500 (not $875,000)

Full root cause analysis documentation: `docs/BugRootCauseAnalysis.md`

---

## 📡 Running the Application

### Prerequisites

1. Install [Java 17+](https://www.java.com/en/)
2. Install [Maven](https://maven.apache.org/) with your IDE
3. Install [Node](https://nodejs.org/en)
4. Use an IDE - [Visual Studio Code](https://code.visualstudio.com/) or [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)

### Backend

```
cd backend
./mvnw spring-boot:run
```

- API: https://localhost:8080
- API Documentation: https://localhost:8080/swagger-ui/index.html
- Health Check: https://localhost:8080/actuator/health

### Frontend

```
cd frontend
npm install
npm run dev
```

- APP: https://localhost:5173

---

## 🧪 Running Tests

```
cd backend
./mvnw clean test
```

---

## ⚠️ Error Handling

| Status Code | Description                              |
| ----------- | ---------------------------------------- |
| 200         | OK - request successful                  |
| 201         | Created - resource created successfully  |
| 400         | Bad Request - invalid input              |
| 401         | Unauthorized - invalid or missing token  |
| 403         | Forbidden - insufficient permissions     |
| 404         | Not Found - resources not found          |
| 500         | Internal Server Error - unexpected error |

Custom exceptions are thrown for each entity (e.g `TradeNotFoundException` with descriptive error messages).

---
