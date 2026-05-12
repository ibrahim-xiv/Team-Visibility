package com.TeamVisibility.App.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TeamVisibility.App.model.User;
import com.TeamVisibility.App.service.UserService;

/**
 * User REST API.
 *
 * Replaces the Thymeleaf-flavored UserController from feature/login which
 * returned "redirect:/..." view names. The static frontend (extension/frontend +
 * extensions/map) calls these endpoints with fetch().
 */
@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    /** Request payload for /login (so we don't expose passwordHash on User). */
    public static class LoginRequest {
        public String usernameOrEmail;
        public String password;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            User u = new User();
            u.setUsername(body.getOrDefault("username",
                body.getOrDefault("email", ""))); // fallback if username omitted
            u.setFirstName(body.getOrDefault("firstName",
                body.getOrDefault("firstname", "")));
            u.setLastName(body.getOrDefault("lastName",
                body.getOrDefault("lastname", "")));
            u.setEmail(body.get("email"));
            // Plain-text storage - see UserService docs for rationale.
            u.setPasswordHash(body.get("password"));

            User saved = userService.register(u);
            Map<String, Object> out = new HashMap<>();
            out.put("id", saved.getId());
            out.put("username", saved.getUsername());
            out.put("email", saved.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(out);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String key = body.getOrDefault("usernameOrEmail",
            body.getOrDefault("username",
                body.getOrDefault("email", "")));
        String password = body.get("password");

        Optional<User> userOpt = userService.login(key, password);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid credentials"));
        }
        User u = userOpt.get();
        Map<String, Object> out = new HashMap<>();
        out.put("id", u.getId());
        out.put("username", u.getUsername());
        out.put("email", u.getEmail());
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        // Light projection - never return passwordHash over the wire.
        return userService.findById(id)
            .map(u -> ResponseEntity.ok((Object) Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "email", u.getEmail())))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "User not found: " + id)));
    }
}
