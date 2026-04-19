package com.TeamVisibility.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TeamVisibility.App.model.Meeting;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}