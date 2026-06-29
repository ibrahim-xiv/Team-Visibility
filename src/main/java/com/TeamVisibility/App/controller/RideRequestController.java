package com.TeamVisibility.App.controller;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TeamVisibility.App.model.RideRequest;
import com.TeamVisibility.App.model.Notification;
import com.TeamVisibility.App.model.User;
import com.TeamVisibility.App.repository.RideRequestRepository;
import com.TeamVisibility.App.repository.NotificationRepository;
import com.TeamVisibility.App.service.UserService;

@RestController
@RequestMapping("/api/rides")
public class RideRequestController {
    private final RideRequestRepository rideRepo;
    private final NotificationRepository notificationRepo;
    private final UserService userService;

    public RideRequestController(RideRequestRepository rr, NotificationRepository nr, UserService us) {
        this.rideRepo = rr;
        this.notificationRepo = nr;
        this.userService = us;
    }

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

        String matcherName = "Jemand";
        try {
            User matcher = userService.findById(uid);
            matcherName = matcher.getFirstName() + " " + matcher.getLastName();
        } catch (Exception ignored) {}

        Long notifyUserId;
        if ("OFFER".equals(r.getType())) {
            r.setRequesterId(uid);
            notifyUserId = r.getOffererId();
        } else {
            r.setOffererId(uid);
            notifyUserId = r.getRequesterId();
        }
        r.setStatus("MATCHED");
        rideRepo.save(r);

        if (notifyUserId != null) {
            Notification n = new Notification();
            n.setUserId(notifyUserId);
            n.setMeetingId(r.getMeetingId());
            if ("OFFER".equals(r.getType())) {
                n.setMessage(matcherName + " fährt bei deiner Mitfahrgelegenheit mit!");
            } else {
                n.setMessage(matcherName + " bietet dir eine Mitfahrt an!");
            }
            notificationRepo.save(n);
        }

        return ResponseEntity.ok(r);
    }
}
