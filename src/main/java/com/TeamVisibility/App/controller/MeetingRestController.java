package com.TeamVisibility.App.controller;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TeamVisibility.App.model.Meeting;
import com.TeamVisibility.App.service.MeetingService;
import com.TeamVisibility.App.service.NotificationService;

@RestController
@RequestMapping("/api/meetings")
public class MeetingRestController {
    private final MeetingService meetingService;
    private final NotificationService notificationService;

    public MeetingRestController(MeetingService ms, NotificationService ns) {
        this.meetingService = ms;
        this.notificationService = ns;
    }

    @GetMapping
    public List<Meeting> listAll() { return meetingService.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        Meeting m = meetingService.findById(id);
        if (m == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(m);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Meeting meeting, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Bitte einloggen"));
        meeting.setCreatorId(userId);
        if (meeting.getNonProfit() == null) meeting.setNonProfit(true);
        try {
            Meeting saved = meetingService.createMeeting(meeting);
            notificationService.notifyAllUsersAboutNewEvent(userId, saved.getId(), saved.getTitle());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Meeting incoming, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Meeting existing = meetingService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        if (!existing.getCreatorId().equals(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Kein Zugriff"));
        return ResponseEntity.ok(meetingService.update(id, incoming));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Meeting existing = meetingService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        if (!existing.getCreatorId().equals(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Kein Zugriff"));
        meetingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
