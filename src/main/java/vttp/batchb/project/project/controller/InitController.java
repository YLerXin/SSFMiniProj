package vttp.batchb.project.project.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vttp.batchb.project.project.util.BookInitializer;

@RestController
public class InitController {

    private final BookInitializer bookInitializer;

    public InitController(BookInitializer bookInitializer) {
        this.bookInitializer = bookInitializer;
    }

    @GetMapping("/initialize")
    public String initializeBooks(@RequestParam String userEmail) {
        try {
            bookInitializer.initForUser(userEmail);
            return "Books initialized for user: " + userEmail;
        } catch (IOException e) {
            return "Failed to initialize books: " + e.getMessage();
        }
    }
}

