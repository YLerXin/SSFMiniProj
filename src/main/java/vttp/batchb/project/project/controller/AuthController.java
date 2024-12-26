package vttp.batchb.project.project.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;
import vttp.batchb.project.project.repository.UserRepository;
import vttp.batchb.project.project.util.BookInitializer;

@Controller
public class AuthController {

    private final UserRepository userRepo;
    private final BookInitializer bookInitializer;


    public AuthController(UserRepository userRepo,BookInitializer bookInitializer) {
        this.userRepo = userRepo;
        this.bookInitializer = bookInitializer;
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session) {
        String storedPass = userRepo.getPassword(email);

        System.out.println("User attempting login: " + email);

        if (storedPass != null && storedPass.equals(password)) {
            session.setAttribute("loggedInUser", email);
            System.out.println("User logged in successfully: " + email);

            try {
                bookInitializer.initForUser(email);
            } catch (IOException e) {
                System.err.println("Failed to initialize books for user " + email + ": " + e.getMessage());
                return "redirect:/?error=Failed%20to%20initialize%20books";
            }

            return "redirect:/books";
        } else {
            System.err.println("Invalid login attempt for user: " + email);
            return "redirect:/?error=Invalid%20credentials";
        }
    }

    @PostMapping("/signup")
    public String handleSignup(@RequestParam String email,
                               @RequestParam String password) {
        if (userRepo.userExists(email)) {
            System.err.println("Signup attempt for existing user: " + email);
            return "redirect:/?error=User%20already%20exists";
        }

        userRepo.saveUser(email, password);
        System.out.println("New user created: " + email);
        return "redirect:/?success=Account%20created";
    }

    @GetMapping("/signup")
    public String showSignupForm() {
        return "signup"; 
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        System.out.println("User logged out. Session invalidated.");
        return "redirect:/?success=Logged%20out";
    }
}
