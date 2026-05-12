package com.TeamVisibility.App.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TeamVisibility.App.model.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(Long userId);

    Optional<Favorite> findByUserIdAndMeetingId(Long userId, Long meetingId);

    void deleteByUserIdAndMeetingId(Long userId, Long meetingId);
}
