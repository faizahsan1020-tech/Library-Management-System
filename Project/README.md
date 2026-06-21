# 📚 Library Management System
### Spring Boot (Java) + HTML Frontend

---

## Project Structure

```
library-project/
│
├── index.html                          ← Frontend (open in browser)
│
├── pom.xml                             ← Maven build file
│
└── src/main/java/com/library/
    ├── LibraryApplication.java         ← Spring Boot entry point
    ├── LibraryService.java             ← ALL DSA Logic (Linked List, Queue, Stack, Bubble Sort)
    └── LibraryController.java          ← REST API endpoints
```

---

## How to Run

### Requirements
- Java 17 or higher
- Maven installed  
  Download: https://maven.apache.org/download.cgi

---

### Step 1 — Start Java Backend

Open terminal in the `library-project` folder and run:

```bash
mvn spring-boot:run
```

You will see:
```
Started LibraryApplication on port 8080
```

✅ Java backend is now running at: `http://localhost:8080`

---

### Step 2 — Open HTML Frontend

Just **double-click** `index.html` to open it in your browser.

OR right-click → Open With → Chrome / Firefox / Edge

The HTML will connect to `http://localhost:8080/api` automatically.

---

## API Endpoints (Java Backend)

| Method | URL                        | Action              |
|--------|----------------------------|---------------------|
| POST   | /api/books                 | Add Book            |
| GET    | /api/books                 | Get All Books       |
| GET    | /api/books/{id}            | Search Book         |
| PUT    | /api/books/{id}            | Update Book         |
| DELETE | /api/books/{id}            | Remove Book         |
| POST   | /api/books/{id}/issue      | Issue / Waitlist    |
| POST   | /api/books/{id}/return     | Return Book         |
| GET    | /api/books/sort            | Sort (Bubble Sort)  |
| GET    | /api/statistics            | Statistics          |
| GET    | /api/history               | Transaction History |

---

## DSA Structures Used

| Structure        | Where Used                        |
|-----------------|-----------------------------------|
| Singly Linked List | Book catalog (head pointer)    |
| Queue (FIFO)    | Student waitlist per book         |
| Stack (LIFO)    | Transaction history log           |
| Bubble Sort     | Sort books alphabetically by title|

---

## Troubleshooting

**"Cannot connect to Java backend"** warning in HTML?
→ Make sure `mvn spring-boot:run` is running in terminal first.

**Port 8080 already in use?**
→ Change port in `src/main/resources/application.properties`:
```
server.port=9090
```
And update the first line in `index.html`:
```js
const API = 'http://localhost:9090/api';
```
