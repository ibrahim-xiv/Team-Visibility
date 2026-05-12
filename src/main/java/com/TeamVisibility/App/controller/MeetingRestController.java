package com.TeamVisibility.App.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TeamVisibility.App.model.Meeting;
import com.TeamVisibility.App.service.MeetingService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meetings")
public class MeetingRestController {

    private final MeetingService meetingService;

    public MeetingRestController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    // Steve: AUFGABE 1 - Daten von Website in DB speichern
    @PostMapping
    public ResponseEntity<?> createMeeting(@RequestBody Meeting meeting) {
        try {
            Meeting saved = meetingService.createMeeting(meeting);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Steve: AUFGABE 2 - Event-Liste anzeigen (Fenster 1)
    @GetMapping
    public ResponseEntity<List<Meeting>> getAllMeetings() {
        return ResponseEntity.ok(meetingService.findAll());
    }

    // Steve: AUFGABE 3 - Kategorie-Sortierung (Fenster 2)
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Meeting>> getMeetingsByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(meetingService.findByCategoryId(categoryId));
    }
}