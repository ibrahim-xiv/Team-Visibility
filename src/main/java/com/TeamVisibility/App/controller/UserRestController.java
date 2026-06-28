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

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registriert den User (unverified) UND sendet den Code in einem Schritt.
     * Wird beim Klick auf "Code senden" aufgerufen.
     */
    @PostMapping("/register-and-send-code")
    public ResponseEntity<?> registerAndSendCode(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");

            // User anlegen falls noch nicht vorhanden
            if (!userService.existsByEmail(email)) {
                User u = new User();
                u.setUsername(body.getOrDefault("username", email));
                u.setFirstName(body.getOrDefault("firstName", body.getOrDefault("firstname", "")));
                u.setLastName(body.getOrDefault("lastName", body.getOrDefault("lastname", "")));
                u.setEmail(email);
                u.setPasswordHash(body.get("password"));
                userService.register(u);
            }

            userService.sendVerificationCode(email);
            return ResponseEntity.ok(Map.of("message", "Code gesendet"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "E-Mail konnte nicht gesendet werden: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            User u = new User();
            u.setUsername(body.getOrDefault("username", body.getOrDefault("email", "")));
            u.setFirstName(body.getOrDefault("firstName", body.getOrDefault("firstname", "")));
            u.setLastName(body.getOrDefault("lastName", body.getOrDefault("lastname", "")));
            u.setEmail(body.get("email"));
            u.setPasswordHash(body.get("password"));

            User saved = userService.register(u);
            Map<String, Object> out = new HashMap<>();
            out.put("id", saved.getId());
            out.put("username", saved.getUsername());
            out.put("email", saved.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(out);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> body) {
        try {
            userService.sendVerificationCode(body.get("email"));
            return ResponseEntity.ok(Map.of("message", "Code gesendet"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "E-Mail konnte nicht gesendet werden: " + e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> body) {
        try {
            userService.verifyCode(body.get("email"), body.get("code"));
            return ResponseEntity.ok(Map.of("message", "Verifizierung erfolgreich"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String key = body.getOrDefault("usernameOrEmail",
            body.getOrDefault("username", body.getOrDefault("email", "")));
        String password = body.get("password");

        Optional<User> userOpt = userService.login(key, password);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Ungültige Anmeldedaten"));
        }
        User u = userOpt.get();
        if (!Boolean.TRUE.equals(u.getVerified())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Bitte zuerst E-Mail verifizieren", "needsVerification", true));
        }
        Map<String, Object> out = new HashMap<>();
        out.put("id", u.getId());
        out.put("username", u.getUsername());
        out.put("email", u.getEmail());
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return userService.findById(id)
            .map(u -> ResponseEntity.ok((Object) Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "email", u.getEmail())))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "User nicht gefunden: " + id)));
    }
}