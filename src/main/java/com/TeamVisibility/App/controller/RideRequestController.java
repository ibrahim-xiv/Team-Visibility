package com.TeamVisibility.App.controller;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TeamVisibility.App.model.RideRequest;
import com.TeamVisibility.App.repository.RideRequestRepository;

@RestController
@RequestMapping("/api/rides")
public class RideRequestController {
    private final RideRequestRepository rideRepo;
    public RideRequestController(RideRequestRepository rr) { this.rideRepo = rr; }

    @GetMapping("/{meetingId}")
    public List<RideRequest> forMeeting(@PathVariable Long meetingId) {
        return rideRepo.findByMeetingId(meetingId);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RideRequest ride, HttpSession session) {
        Long uid = (Long) session.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Bitte einloggen"));
        if ("OFFER".equals(ride.getType())) ride.setOffererId(uid);
        else ride.setRequesterId(uid);
        return ResponseEntity.status(HttpStatus.CREATED).body(rideRepo.save(ride));
    }

    @PutMapping("/{id}/match")
    public ResponseEntity<?> match(@PathVariable Long id, HttpSession session) {
        Long uid = (Long) session.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        RideRequest r = rideRepo.findById(id).orElse(null);
        if (r == null) return ResponseEntity.notFound().build();
        if ("OFFER".equals(r.getType())) r.setRequesterId(uid);
        else r.setOffererId(uid);
        r.setStatus("MATCHED");
        return ResponseEntity.ok(rideRepo.save(r));
    }
}
