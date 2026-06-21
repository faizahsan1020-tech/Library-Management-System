package com.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * LibraryController.java
 * ─────────────────────────────────────────────────
 * REST API endpoints — HTML frontend calls these.
 * All actual logic stays in LibraryService.java
 * ─────────────────────────────────────────────────
 * Base URL: http://localhost:8080/api
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")   // allows HTML file to call this API
public class LibraryController {

    @Autowired
    private LibraryService library;

    // POST /api/books — Add Book
    @PostMapping("/books")
    public ResponseEntity<String> addBook(@RequestBody Map<String, Object> body) {
        int    id     = ((Number) body.get("bookId")).intValue();
        String title  = (String) body.get("title");
        String author = (String) body.get("author");
        String result = library.addBook(id, title, author);
        if (result.startsWith("ERROR")) return ResponseEntity.badRequest().body(result);
        return ResponseEntity.ok(result);
    }

    // GET /api/books — Get All Books
    @GetMapping("/books")
    public List<LibraryService.BookDTO> getAllBooks() {
        return library.getAllBooks();
    }

    // GET /api/books/{id} — Search Book
    @GetMapping("/books/{id}")
    public ResponseEntity<?> searchBook(@PathVariable int id) {
        LibraryService.BookDTO book = library.searchBook(id);
        if (book == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(book);
    }

    // PUT /api/books/{id} — Update Book
    @PutMapping("/books/{id}")
    public ResponseEntity<String> updateBook(@PathVariable int id,
                                              @RequestBody Map<String, String> body) {
        String result = library.updateBook(id, body.get("title"), body.get("author"));
        if (result.startsWith("ERROR")) return ResponseEntity.badRequest().body(result);
        return ResponseEntity.ok(result);
    }

    // DELETE /api/books/{id} — Remove Book
    @DeleteMapping("/books/{id}")
    public ResponseEntity<String> removeBook(@PathVariable int id) {
        String result = library.removeBook(id);
        if (result.startsWith("ERROR")) return ResponseEntity.badRequest().body(result);
        return ResponseEntity.ok(result);
    }

    // POST /api/books/{id}/issue — Issue Book (with optional student name)
    @PostMapping("/books/{id}/issue")
    public ResponseEntity<String> issueBook(@PathVariable int id,
                                             @RequestBody(required = false) Map<String, String> body) {
        String student = (body != null) ? body.get("studentName") : null;
        String result  = library.issueBook(id, student);
        if (result.startsWith("ERROR")) return ResponseEntity.badRequest().body(result);
        return ResponseEntity.ok(result);
    }

    // POST /api/books/{id}/return — Return Book
    @PostMapping("/books/{id}/return")
    public ResponseEntity<String> returnBook(@PathVariable int id) {
        String result = library.returnBook(id);
        if (result.startsWith("ERROR")) return ResponseEntity.badRequest().body(result);
        return ResponseEntity.ok(result);
    }

    // GET /api/books/sort — Sort books alphabetically (Bubble Sort)
    @GetMapping("/books/sort")
    public List<LibraryService.BookDTO> sortBooks() {
        return library.sortByTitle();
    }

    // GET /api/statistics — Library Stats
    @GetMapping("/statistics")
    public LibraryService.StatsDTO getStatistics() {
        return library.getStatistics();
    }

    // GET /api/history — Transaction History Stack
    @GetMapping("/history")
    public List<String> getHistory() {
        return library.getHistory();
    }
}
