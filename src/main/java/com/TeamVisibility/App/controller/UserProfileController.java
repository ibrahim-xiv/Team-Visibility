package com.TeamVisibility.App.controller;

import java.util.Map;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TeamVisibility.App.dto.UserProfileResponse;
import com.TeamVisibility.App.model.User;
import com.TeamVisibility.App.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {
    private final UserService userService;
    public UserProfileController(UserService us) { this.userService = us; }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        try {
            User u = userService.findById(id);
            return ResponseEntity.ok(UserProfileResponse.from(u));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
