package vttp.batchb.project.project.controller;

import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import vttp.batchb.project.project.model.Book;
import vttp.batchb.project.project.service.BookService;
import vttp.batchb.project.project.service.TitleBasedSummarizerService;

@Controller
@RequestMapping
public class BookController {

    private final BookService bookService;
    private final TitleBasedSummarizerService titleSummarizer;

    public BookController(BookService bookService, TitleBasedSummarizerService titleSummarizer) {
        this.bookService = bookService;
        this.titleSummarizer = titleSummarizer;
    }

    @GetMapping("/books")
    public String listBooks(Model model, HttpSession session) {
        String userEmail = (String) session.getAttribute("loggedInUser");
        if (userEmail == null) {
            return "redirect:/?error=Please%20login%20first";
        }
        model.addAttribute("books", bookService.getAllBooks(userEmail));
        return "books-list";
    }

    @GetMapping("/book/read/{id}")
    public String readBook(@PathVariable Integer id,
                           Model model,
                           HttpSession session) throws IOException {
        String userEmail = (String) session.getAttribute("loggedInUser");
        if (userEmail == null) {
            return "redirect:/?error=Please%20login%20first";
        }
        String textContent = bookService.getBookText(userEmail, id);
        model.addAttribute("textContent", textContent);
        model.addAttribute("bookId", id);
        String fontSize = bookService.getBookFontSize(userEmail, id);
        model.addAttribute("fontSize", fontSize);
        return "read-book";
    }

    @GetMapping("/books/search")
    public String searchBooks(@RequestParam String query,
                              Model model,
                              HttpSession session) throws IOException {
        String userEmail = (String) session.getAttribute("loggedInUser");
        if (userEmail == null) {
            return "redirect:/?error=Please%20login%20first";
        }
        List<Book> addedBooks = bookService.searchAndAddBooks(userEmail, query);
        model.addAttribute("books", bookService.getAllBooks(userEmail));
        model.addAttribute("message", addedBooks.size() + " book(s) added from search: " + query);
        return "books-list";
    }

    @GetMapping("/")
    public String showLandingPage(Model model) {
        return "landing";
    }

    @GetMapping("/book/delete/{id}")
    public String deleteBook(@PathVariable Integer id, HttpSession session) {
        String userEmail = (String) session.getAttribute("loggedInUser");
        if (userEmail == null) {
            return "redirect:/?error=Please%20login%20first";
        }
        bookService.deleteBook(userEmail, id);
        return "redirect:/books?success=Book%20deleted";
    }

    @GetMapping("/summary/{id}")
    public String summarizeBookById(@PathVariable Integer id,
                                    HttpSession session,
                                    Model model) {
        String userEmail = (String) session.getAttribute("loggedInUser");
        if (userEmail == null) {
            return "redirect:/?error=Please%20login%20first";
        }
        Book book = bookService.getBookById(userEmail, id);
        if (book == null) {
            model.addAttribute("summary", "(No book found for ID " + id + ")");
            model.addAttribute("bookTitle", "Unknown");
            return "summary-page";
        }
        String bookTitle = book.getTitle();
        String finalSummary = titleSummarizer.summarizeByTitle(bookTitle);
        model.addAttribute("summary", finalSummary);
        model.addAttribute("bookTitle", bookTitle);
        return "summary-page";
    }

    @PostMapping("/book/markread/{id}")
    public String markAsRead(@PathVariable Integer id, HttpSession session) {
        String userEmail = (String) session.getAttribute("loggedInUser");
        if (userEmail == null) {
            return "redirect:/?error=Please%20login%20first";
        }
        bookService.markBookAsRead(userEmail, id);
        return "redirect:/books?success=Book%20marked%20as%20read";
    }

    @PostMapping("/book/readFontSize/{id}")
    public String setBookFontSize(@PathVariable Integer id,
                                  @RequestParam String fontSize,
                                  HttpSession session) {
        String userEmail = (String) session.getAttribute("loggedInUser");
        if (userEmail == null) {
            return "redirect:/?error=Please%20login%20first";
        }
        bookService.setBookFontSize(userEmail, id, fontSize);
        return "redirect:/book/read/" + id;
    }
}
