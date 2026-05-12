package com.TeamVisibility.App.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Favorite (user-saves-meeting) join entity.
 *
 * Merged from feature/favorite. Unique constraint added so the same user
 * cannot favorite the same meeting twice (the branch did not enforce this,
 * but the REST endpoints assume it).
 */
@Entity
@Table(
    name = "favorites",
    uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "meetingId"})
)
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long meetingId;

    public Favorite() {
    }

    public Favorite(Long userId, Long meetingId) {
        this.userId = userId;
        this.meetingId = meetingId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }
}
