package com.TeamVisibility.App.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TeamVisibility.App.model.Meeting;
import com.TeamVisibility.App.service.MeetingService;

/**
 * Meeting REST API.
 *
 * Merged from feature/meeting-object MeetingRestController.
 * The Thymeleaf MeetingController (@Controller with view names) was
 * removed during integration per the "one consistent API strategy:
 * @RestController" requirement.
 */
@RestController
@RequestMapping("/api/meetings")
public class MeetingRestController {

    private final MeetingService meetingService;

    public MeetingRestController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

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

    @GetMapping
    public ResponseEntity<List<Meeting>> getAllMeetings() {
        return ResponseEntity.ok(meetingService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMeeting(@PathVariable Long id) {
        Meeting m = meetingService.findById(id);
        if (m == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Meeting not found: " + id));
        }
        return ResponseEntity.ok(m);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Meeting>> getMeetingsByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(meetingService.findByCategoryId(categoryId));
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<Meeting>> getMeetingsByCreator(
            @PathVariable Long creatorId) {
        return ResponseEntity.ok(meetingService.findByCreatorId(creatorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMeeting(
            @PathVariable Long id, @RequestBody Meeting meeting) {
        try {
            return ResponseEntity.ok(meetingService.update(id, meeting));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        meetingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
