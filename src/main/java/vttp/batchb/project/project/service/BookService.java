package vttp.batchb.project.project.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import vttp.batchb.project.project.model.Book;
import vttp.batchb.project.project.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepo;
    private final RestTemplate restTemplate;

    public BookService(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
        this.restTemplate = new RestTemplate();
    }

    public List<Book> getAllBooks(String userEmail) {
        List<Book> books = bookRepo.getAllBooks(userEmail);
        
        for (Book b : books) {
            boolean isRead = bookRepo.isBookRead(userEmail, b.getId());
            b.setRead(isRead);
        }

        return books;
    }

    public Book addBookById(String userEmail, Integer id) throws IOException {
        String url = "https://gutendex.com/books/" + id;
        String jsonResp = restTemplate.getForObject(URI.create(url), String.class);
        if (jsonResp == null || jsonResp.isBlank()) {
            return null;
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(jsonResp.getBytes(StandardCharsets.UTF_8));
             JsonReader reader = Json.createReader(bais)) {

            JsonObject root = reader.readObject();
            if (!root.containsKey("id")) {
                return null;
            }

            int bookId = root.getInt("id", -1);
            String title = root.getString("title", "");

            JsonObject formats = root.getJsonObject("formats");
            String coverImageUrl = (formats != null) ? formats.getString("image/jpeg", "") : "";
            String textUrl = (formats != null) ? formats.getString("text/plain; charset=us-ascii", "") : "";

            Book b = new Book(bookId, title, coverImageUrl, textUrl);
            bookRepo.saveBook(userEmail, b);
            return b;
        }
    }

    public List<Book> searchAndAddBooks(String userEmail, String query) throws IOException {
        String url = "https://gutendex.com/books?search=" + query.replace(" ", "%20");
        String jsonResp = restTemplate.getForObject(URI.create(url), String.class);
        if (jsonResp == null || jsonResp.isBlank()) {
            return List.of();
        }

        List<Book> addedBooks = new ArrayList<>();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(jsonResp.getBytes(StandardCharsets.UTF_8));
             JsonReader reader = Json.createReader(bais)) {

            JsonObject root = reader.readObject();
            JsonArray results = root.getJsonArray("results");
            if (results == null || results.isEmpty()) {
                return addedBooks;
            }

            for (int i = 0; i < results.size(); i++) {
                JsonObject n = results.getJsonObject(i);

                int bookId = n.getInt("id", -1);
                String title = n.getString("title", "");
                JsonObject formats = n.getJsonObject("formats");
                String coverImageUrl = (formats != null) ? formats.getString("image/jpeg", "") : "";
                String textUrl = (formats != null) ? formats.getString("text/plain; charset=us-ascii", "") : "";

                if (!textUrl.isEmpty()) {
                    Book b = new Book(bookId, title, coverImageUrl, textUrl);
                    bookRepo.saveBook(userEmail, b);
                    addedBooks.add(b);
                    break;
                }
            }
        }

        return addedBooks;
    }

    public String getBookText(String userEmail, Integer id) throws IOException {
        Book book = bookRepo.getBookById(userEmail, id);
        if (book == null || book.getTextUrl() == null || book.getTextUrl().isEmpty()) {
            return "No text available for this book.";
        }

        String currentUrl = book.getTextUrl();
        int redirectCount = 0;
        int maxRedirects = 10;

        while (true) {
            ResponseEntity<String> response = restTemplate.getForEntity(currentUrl, String.class);
            if (response.getStatusCode().is3xxRedirection()) {
                String redirectUrl = response.getHeaders().getFirst(HttpHeaders.LOCATION);
                if (redirectUrl == null || redirectUrl.isEmpty()) {
                    return "Redirected but no Location header found.";
                }
                currentUrl = redirectUrl;
                redirectCount++;
                if (redirectCount > maxRedirects) {
                    return "Too many redirects encountered.";
                }
            } else {
                if (response.getBody() == null || response.getBody().isEmpty()) {
                    return "No text content found.";
                }
                return response.getBody();
            }
        }
    }

    public void deleteBook(String userEmail, Integer id) {
        bookRepo.deleteBook(userEmail, id);
    }

    public Book getBookById(String userEmail, Integer id) {
        return bookRepo.getBookById(userEmail, id);
    }

    public void markBookAsRead(String userEmail, Integer id) {
        bookRepo.markBookAsRead(userEmail, id);
    }

    public boolean isBookRead(String userEmail, Integer id) {
        return bookRepo.isBookRead(userEmail, id);
    }

    public void setBookFontSize(String userEmail, Integer id, String fontSize) {
        bookRepo.setBookFontSize(userEmail, id, fontSize);
    }

    public String getBookFontSize(String userEmail, Integer id) {
        return bookRepo.getBookFontSize(userEmail, id);
    }
}
