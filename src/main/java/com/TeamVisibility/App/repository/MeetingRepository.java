package com.TeamVisibility.App.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TeamVisibility.App.model.Meeting;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    /** From feature/meeting-object: filter meetings by category. */
    List<Meeting> findByCategoryId(Long categoryId);

    /** Convenience: meetings created by a given user. */
    List<Meeting> findByCreatorId(Long creatorId);
}
