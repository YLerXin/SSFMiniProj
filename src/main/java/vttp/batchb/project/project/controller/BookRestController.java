package vttp.batchb.project.project.controller;

import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import vttp.batchb.project.project.model.Book;
import vttp.batchb.project.project.service.BookService;

@RestController
public class BookRestController {

    private final BookService bookService;

    public BookRestController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/api/book/add/{id}")
    public Book addBookById(@PathVariable Integer id, HttpSession session) throws IOException {
        String userEmail = (String) session.getAttribute("loggedInUser");
        if (userEmail == null) {
            throw new IllegalStateException("User not logged in");
        }
        return bookService.addBookById(userEmail, id);
    }

    @GetMapping("/api/book/search/{query}")
    public List<Book> searchBooks(@PathVariable String query, HttpSession session) throws IOException {
        String userEmail = (String) session.getAttribute("loggedInUser");
        if (userEmail == null) {
            throw new IllegalStateException("User not logged in");
        }
        return bookService.searchAndAddBooks(userEmail, query);
    }
}
