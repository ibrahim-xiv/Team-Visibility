package com.TeamVisibility.App.model; // Passe den Package-Namen an dein Projekt an

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String meetingDate; // Fürs MVP reicht String, später LocalDateTime
    private String location;
    private boolean isNonProfit;

    // Leerer Konstruktor für Spring
    public Meeting() {}

    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMeetingDate() { return meetingDate; }
    public void setMeetingDate(String meetingDate) { this.meetingDate = meetingDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public boolean isNonProfit() { return isNonProfit; }
    public void setNonProfit(boolean isNonProfit) { this.isNonProfit = isNonProfit; }
}