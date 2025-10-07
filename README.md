# ISTQB Quiz Application

This is a full-stack web application designed to help users practice for ISTQB certification exams. It features a Vue.js frontend and a Spring Boot backend.

## Features

*   Import exams from JSON files.
*   List available exams.
*   Take exams and see your score.
*   Review previous attempts.
*   Export exams to JSON.
*   Delete exams.

## Technologies Used

### Frontend

*   **Vue.js:** A progressive JavaScript framework for building user interfaces.
*   **Vite:** A fast build tool and development server for modern web projects.
*   **Vue Router:** The official router for Vue.js.
*   **Pinia:** The official state management library for Vue.js.
*   **Axios:** A promise-based HTTP client for the browser and Node.js.

### Backend

*   **Java:** A general-purpose, class-based, object-oriented programming language.
*   **Spring Boot:** A framework for creating stand-alone, production-grade Spring-based applications.
*   **Maven:** A build automation tool used primarily for Java projects.
*   **Spring Data JPA:** Part of the larger Spring Data family, it makes it easy to easily implement JPA based repositories.
*   **H2 Database:** An in-memory, relational database management system written in Java.
*   **Lombok:** A Java library that automatically plugs into your editor and build tools to automate boilerplate code.

## Project Structure

```
istqb/
├── quiz-backend/      # Spring Boot backend application
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/quizbackend/
│   │       │   ├── config/
│   │       │   ├── controller/
│   │       │   ├── model/
│   │       │   ├── repository/
│   │       │   └── service/
│   │       └── resources/
│   │           ├── istqb-mock-exams-en-pretty/  # Mock exam data
│   │           └── application.properties     # Spring Boot configuration
│   └── pom.xml
└── quiz-frontend/     # Vue.js frontend application
    ├── src/
    │   ├── components/
    │   ├── router/
    │   ├── stores/
    │   ├── utils/
    │   └── views/
    └── package.json
```

## API Endpoints

The backend exposes the following REST API endpoints:

### Exam Endpoints

*   `POST /api/exams/import`: Imports a new exam from a JSON object.
*   `GET /api/exams`: Retrieves a list of all available exams.
*   `GET /api/exams/{id}`: Retrieves a specific exam by its ID.
*   `DELETE /api/exams/{id}`: Deletes an exam by its ID.
*   `GET /api/exams/export/{id}`: Exports an exam to a JSON object.

### Attempt Endpoints

*   `POST /api/attempts/submit`: Submits an exam attempt and returns the score.

## Configuration

### Backend

The backend configuration is located in `quiz-backend/src/main/resources/application.properties`.

*   `server.port`: The port on which the backend server runs (default: `8081`).
*   `spring.datasource.url`: The JDBC URL of the database (default: `jdbc:h2:mem:quizdb`).
*   `spring.h2.console.enabled`: Enables the H2 database console (default: `true`).
*   `spring.h2.console.path`: The path to the H2 database console (default: `/h2-console`).

### Frontend

The frontend configuration is located in `quiz-frontend/vite.config.js`. The frontend communicates with the backend at the URL configured in the source code.

## Getting Started

### Prerequisites

*   Node.js and npm (for the frontend)
*   Java 17 and Maven (for the backend)

### Backend Setup

1.  Navigate to the `quiz-backend` directory:
    ```bash
    cd quiz-backend
    ```
2.  Run the application using Maven:
    ```bash
    ./mvnw spring-boot:run
    ```
The backend will be running on `http://localhost:8081`.

### Frontend Setup

1.  Navigate to the `quiz-frontend` directory:
    ```bash
    cd quiz-frontend
    ```
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Start the development server:
    ```bash
    npm run dev
    ```
The frontend will be running on `http://localhost:5173` (or another port if 5173 is busy).

## Usage

1.  Open the application in your browser.
2.  Import an exam using the "Import Exam" feature. You can use the mock exam files located in `quiz-backend/src/main/resources/istqb-mock-exams-en-pretty/`.
3.  Once imported, the exam will appear in the "Exam List".
4.  You can take the exam, and upon completion, you will see your score.

## Contributing

Contributions are welcome! Please feel free to submit a pull request or open an issue if you have any suggestions or find any bugs.

1.  Fork the repository.
2.  Create a new branch (`git checkout -b feature/your-feature`).
3.  Make your changes.
4.  Commit your changes (`git commit -m 'Add some feature'`).
5.  Push to the branch (`git push origin feature/your-feature`).
6.  Open a pull request.