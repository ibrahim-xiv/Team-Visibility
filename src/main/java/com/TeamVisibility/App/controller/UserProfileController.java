package com.TeamVisibility.App.controller;

import java.util.*;
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

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> body, HttpSession session) {
        Long uid = (Long) session.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        try {
            User u = userService.findById(uid);
            if (body.containsKey("firstName")) u.setFirstName(body.get("firstName"));
            if (body.containsKey("lastName")) u.setLastName(body.get("lastName"));
            if (body.containsKey("avatar")) u.setAvatar(body.get("avatar"));
            if (body.containsKey("bio")) u.setBio(body.get("bio"));
            if (body.containsKey("locationName")) u.setLocationName(body.get("locationName"));
            if (body.containsKey("interests")) u.setInterests(body.get("interests"));
            userService.saveUser(u);
            return ResponseEntity.ok(UserProfileResponse.from(u));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<?> follow(@PathVariable Long id, HttpSession session) {
        Long uid = (Long) session.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (uid.equals(id)) return ResponseEntity.badRequest().body(Map.of("error", "Du kannst dir nicht selbst folgen"));
        try {
            User target = userService.findById(id);
            Set<String> followers = new LinkedHashSet<>();
            if (target.getFollowers() != null && !target.getFollowers().isEmpty()) {
                followers.addAll(Arrays.asList(target.getFollowers().split(",")));
            }
            followers.add(String.valueOf(uid));
            target.setFollowers(String.join(",", followers));
            userService.saveUser(target);
            return ResponseEntity.ok(Map.of("following", true, "followerCount", followers.size()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/unfollow")
    public ResponseEntity<?> unfollow(@PathVariable Long id, HttpSession session) {
        Long uid = (Long) session.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        try {
            User target = userService.findById(id);
            Set<String> followers = new LinkedHashSet<>();
            if (target.getFollowers() != null && !target.getFollowers().isEmpty()) {
                followers.addAll(Arrays.asList(target.getFollowers().split(",")));
            }
            followers.remove(String.valueOf(uid));
            target.setFollowers(String.join(",", followers));
            userService.saveUser(target);
            return ResponseEntity.ok(Map.of("following", false, "followerCount", followers.size()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/is-following")
    public ResponseEntity<?> isFollowing(@PathVariable Long id, HttpSession session) {
        Long uid = (Long) session.getAttribute("userId");
        if (uid == null) return ResponseEntity.ok(Map.of("following", false));
        try {
            User target = userService.findById(id);
            boolean following = target.getFollowers() != null &&
                Arrays.asList(target.getFollowers().split(",")).contains(String.valueOf(uid));
            return ResponseEntity.ok(Map.of("following", following));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("following", false));
        }
    }

    @GetMapping("/me/following")
    public ResponseEntity<?> myFollowing(HttpSession session) {
        Long uid = (Long) session.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        List<UserProfileResponse> result = new ArrayList<>();
        for (User u : userService.findAll()) {
            if (u.getFollowers() != null &&
                Arrays.asList(u.getFollowers().split(",")).contains(String.valueOf(uid))) {
                result.add(UserProfileResponse.from(u));
            }
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/me/notif-radius")
    public ResponseEntity<?> updateRadius(@RequestBody Map<String, Integer> body, HttpSession session) {
        Long uid = (Long) session.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        try {
            User u = userService.findById(uid);
            u.setNotifRadius(body.get("radius"));
            userService.saveUser(u);
            return ResponseEntity.ok(Map.of("radius", body.get("radius")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
