package com.TeamVisibility.App.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TeamVisibility.App.model.Favorite;
import com.TeamVisibility.App.service.FavoriteService;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteRestController {

    private final FavoriteService favoriteService;

    public FavoriteRestController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public ResponseEntity<?> addFavorite(@RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Long meetingId = body.get("meetingId");
        if (userId == null || meetingId == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "userId and meetingId required"));
        }
        Favorite f = favoriteService.addFavorite(userId, meetingId);
        return ResponseEntity.status(HttpStatus.CREATED).body(f);
    }

    @DeleteMapping("/user/{userId}/meeting/{meetingId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long userId, @PathVariable Long meetingId) {
        favoriteService.removeFavorite(userId, meetingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Favorite>> getFavoritesForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getFavoritesForUser(userId));
    }
}
