package vttp.batchb.project.project.util;

import jakarta.annotation.PostConstruct;
import vttp.batchb.project.project.service.BookService;

import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class BookInitializer {

    private final BookService bookService;

    public BookInitializer(BookService bookService) {
        this.bookService = bookService;
    }
    @PostConstruct
    public void onStartup() {
    }

    public void initForUser(String userEmail) throws IOException {
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("User email cannot be null or empty");
        }
        
        bookService.addBookById(userEmail,84);    // Frankenstein
        bookService.addBookById(userEmail,11);    // A Christmas Carol
        bookService.addBookById(userEmail,174);   // The Picture of Dorian Gray
        bookService.addBookById(userEmail,2701);  // Moby Dick
        bookService.addBookById(userEmail,1513);  // Romeo and Juliet
    }
}
