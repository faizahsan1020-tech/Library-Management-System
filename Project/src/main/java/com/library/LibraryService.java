package com.library;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * LibraryService.java
 * ─────────────────────────────────────────────────────────────
 * All DSA logic from original Main.java:
 *   - Singly Linked List  → Book catalog
 *   - Queue (per Book)    → Student waitlist
 *   - Stack               → Transaction history (LIFO)
 *   - Bubble Sort         → Sort by title
 * ─────────────────────────────────────────────────────────────
 */
@Service
public class LibraryService {

    // ══════════════════════════════════════════════
    // 1. QUEUE NODE — Student Waitlist per Book
    // ══════════════════════════════════════════════
    static class StudentQueueNode {
        String studentName;
        StudentQueueNode next;

        StudentQueueNode(String studentName) {
            this.studentName = studentName;
            this.next = null;
        }
    }

    // ══════════════════════════════════════════════
    // 2. BOOK NODE — Linked List Node
    //    Each Book has its own Queue for waitlist
    // ══════════════════════════════════════════════
    static class Book {
        int bookId;
        String title;
        String author;
        boolean issued;
        Book next;

        // Waitlist Queue pointers (per book)
        StudentQueueNode queueFront = null;
        StudentQueueNode queueRear  = null;

        Book(int bookId, String title, String author) {
            this.bookId = bookId;
            this.title  = title;
            this.author = author;
            this.issued = false;
            this.next   = null;
        }

        // Queue: Enqueue — Join Waitlist
        public void enqueueWaitlist(String name) {
            StudentQueueNode newNode = new StudentQueueNode(name);
            if (queueRear == null) {
                queueFront = queueRear = newNode;
            } else {
                queueRear.next = newNode;
                queueRear = newNode;
            }
        }

        // Queue: Dequeue — Next student in line
        public String dequeueWaitlist() {
            if (queueFront == null) return null;
            String name = queueFront.studentName;
            queueFront = queueFront.next;
            if (queueFront == null) queueRear = null;
            return name;
        }

        // Collect all waitlist names for API response
        public List<String> getWaitlistAsList() {
            List<String> list = new ArrayList<>();
            StudentQueueNode cur = queueFront;
            while (cur != null) {
                list.add(cur.studentName);
                cur = cur.next;
            }
            return list;
        }
    }

    // ══════════════════════════════════════════════
    // 3. HISTORY STACK — LIFO Transaction Log
    // ══════════════════════════════════════════════
    static class HistoryStack {
        static class StackNode {
            String logMessage;
            StackNode next;
            StackNode(String logMessage) {
                this.logMessage = logMessage;
                this.next = null;
            }
        }
        private StackNode top = null;

        // Push new log onto stack
        public void push(String log) {
            StackNode newNode = new StackNode(log);
            newNode.next = top;
            top = newNode;
        }

        // Return all logs as list (LIFO order — newest first)
        public List<String> getAllLogs() {
            List<String> logs = new ArrayList<>();
            StackNode temp = top;
            while (temp != null) {
                logs.add(temp.logMessage);
                temp = temp.next;
            }
            return logs;
        }
    }

    // ══════════════════════════════════════════════
    // 4. LIBRARY ENGINE
    //    Linked List head pointer + History Stack
    // ══════════════════════════════════════════════
    private Book head = null;
    private final HistoryStack history = new HistoryStack();

    // ── FIND BOOK (internal helper) ──────────────
    private Book findBook(int id) {
        Book temp = head;
        while (temp != null) {
            if (temp.bookId == id) return temp;
            temp = temp.next;
        }
        return null;
    }

    // ── ADD BOOK — Append to Linked List ─────────
    public String addBook(int id, String title, String author) {
        if (findBook(id) != null)
            return "ERROR: Book ID " + id + " already exists.";

        Book newBook = new Book(id, title, author);
        if (head == null) {
            head = newBook;
        } else {
            Book temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newBook;
        }
        history.push("Admin added book: '" + title + "' (ID: " + id + ")");
        return "Book '" + title + "' added successfully.";
    }

    // ── ISSUE BOOK — with Queue Waitlist ─────────
    public String issueBook(int id, String studentName) {
        Book book = findBook(id);
        if (book == null) return "ERROR: Book not found.";

        if (book.issued) {
            if (studentName == null || studentName.isBlank())
                return "ALREADY_ISSUED";

            book.enqueueWaitlist(studentName);
            history.push("Student '" + studentName + "' joined waitlist queue for Book ID: " + id);
            return "WAITLIST: " + studentName + " added to waitlist for '" + book.title + "'.";
        } else {
            book.issued = true;
            String msg = "Book '" + book.title + "' issued successfully" +
                         (studentName != null && !studentName.isBlank() ? " to " + studentName : "") + ".";
            history.push("Book ID: " + id + " (" + book.title + ") issued" +
                         (studentName != null && !studentName.isBlank() ? " to " + studentName : "") + ".");
            return msg;
        }
    }

    // ── RETURN BOOK — Auto-assign from Queue ─────
    public String returnBook(int id) {
        Book book = findBook(id);
        if (book == null) return "ERROR: Book not found.";
        if (!book.issued) return "ERROR: Book was not issued.";

        history.push("Book ID: " + id + " was returned to catalog inventory.");
        String nextStudent = book.dequeueWaitlist();

        if (nextStudent != null) {
            book.issued = true;
            history.push("Book ID: " + id + " auto-issued from Queue to: " + nextStudent);
            return "QUEUE_ISSUED: Book returned and auto-issued to next in queue: " + nextStudent;
        } else {
            book.issued = false;
            return "Book '" + book.title + "' returned successfully.";
        }
    }

    // ── SEARCH BOOK ───────────────────────────────
    public BookDTO searchBook(int id) {
        Book book = findBook(id);
        if (book == null) return null;
        return toDTO(book);
    }

    // ── UPDATE BOOK ───────────────────────────────
    public String updateBook(int id, String title, String author) {
        Book book = findBook(id);
        if (book == null) return "ERROR: Book not found.";
        book.title  = title;
        book.author = author;
        history.push("Admin updated details for Book ID: " + id);
        return "Book ID " + id + " updated successfully.";
    }

    // ── REMOVE BOOK — Linked List Deletion ───────
    public String removeBook(int id) {
        if (head == null) return "ERROR: Library is empty.";

        if (head.bookId == id) {
            history.push("Admin removed Book: '" + head.title + "'");
            head = head.next;
            return "Book removed successfully.";
        }
        Book current = head;
        while (current.next != null) {
            if (current.next.bookId == id) {
                history.push("Admin removed Book: '" + current.next.title + "'");
                current.next = current.next.next;
                return "Book removed successfully.";
            }
            current = current.next;
        }
        return "ERROR: Book not found.";
    }

    // ── DISPLAY ALL BOOKS — LL Traversal ─────────
    public List<BookDTO> getAllBooks() {
        List<BookDTO> list = new ArrayList<>();
        Book temp = head;
        while (temp != null) {
            list.add(toDTO(temp));
            temp = temp.next;
        }
        return list;
    }

    // ── SORT BY TITLE — Bubble Sort on LL ────────
    public List<BookDTO> sortByTitle() {
        if (head == null || head.next == null)
            return getAllBooks();

        boolean swapped;
        do {
            swapped = false;
            Book current = head;
            while (current.next != null) {
                if (current.title.compareToIgnoreCase(current.next.title) > 0) {
                    // Swap data (not pointers) — same as original Java
                    int    tmpId     = current.bookId;
                    String tmpTitle  = current.title;
                    String tmpAuthor = current.author;
                    boolean tmpIssued = current.issued;
                    StudentQueueNode tmpFront = current.queueFront;
                    StudentQueueNode tmpRear  = current.queueRear;

                    current.bookId    = current.next.bookId;
                    current.title     = current.next.title;
                    current.author    = current.next.author;
                    current.issued    = current.next.issued;
                    current.queueFront = current.next.queueFront;
                    current.queueRear  = current.next.queueRear;

                    current.next.bookId    = tmpId;
                    current.next.title     = tmpTitle;
                    current.next.author    = tmpAuthor;
                    current.next.issued    = tmpIssued;
                    current.next.queueFront = tmpFront;
                    current.next.queueRear  = tmpRear;

                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);

        history.push("Admin sorted catalog alphabetically by title (Bubble Sort).");
        return getAllBooks();
    }

    // ── STATISTICS ────────────────────────────────
    public StatsDTO getStatistics() {
        int total = 0, issued = 0;
        Book temp = head;
        while (temp != null) {
            total++;
            if (temp.issued) issued++;
            temp = temp.next;
        }
        return new StatsDTO(total, total - issued, issued);
    }

    // ── HISTORY STACK ─────────────────────────────
    public List<String> getHistory() {
        return history.getAllLogs();
    }

    // ══════════════════════════════════════════════
    // DTO CLASSES — Data Transfer Objects for API
    // ══════════════════════════════════════════════
    public static class BookDTO {
        public int     bookId;
        public String  title;
        public String  author;
        public boolean issued;
        public List<String> waitlist;

        public BookDTO(int bookId, String title, String author, boolean issued, List<String> waitlist) {
            this.bookId   = bookId;
            this.title    = title;
            this.author   = author;
            this.issued   = issued;
            this.waitlist = waitlist;
        }
    }

    public static class StatsDTO {
        public int total, available, issued;
        public StatsDTO(int total, int available, int issued) {
            this.total = total; this.available = available; this.issued = issued;
        }
    }

    // Internal helper — Book → DTO
    private BookDTO toDTO(Book b) {
        return new BookDTO(b.bookId, b.title, b.author, b.issued, b.getWaitlistAsList());
    }
}
