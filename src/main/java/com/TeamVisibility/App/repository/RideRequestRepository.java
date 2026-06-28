package com.TeamVisibility.App.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.TeamVisibility.App.model.RideRequest;

public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {
    List<RideRequest> findByMeetingId(Long meetingId);
}
