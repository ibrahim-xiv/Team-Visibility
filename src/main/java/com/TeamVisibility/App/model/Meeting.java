package com.TeamVisibility.App.model; // Passe den Package-Namen an dein Projekt an

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long creator_id;

    private Long category_id;

    private String title;
    private String description;
    private Float lat;
    private Float lng;
    private LocalDateTime date_time;
    private Boolean isNonProfit;


    // Leerer Konstruktor für Spring
    public Meeting() {}

    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCreator_id() {return creator_id; }
    public void setCreator_id(Long creator_id) {this.creator_id = creator_id;}
    public Long getCategory_id() {return category_id;}
    public void setCategory_id(Long category_id) {this.category_id = category_id;}
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public LocalDateTime getDate_time() { return date_time; }
    public void setDate_time(LocalDateTime date_time) { this.date_time = date_time; }
    public Float getLat() { return lat; }
    public void setLat(Float lat) { this.lat = lat; }
    public Float getLng() { return lng; }
    public void setLng(Float lng) { this.lng = lng; }
    public Boolean isNonProfit() { return isNonProfit; }
    public void setNonProfit(boolean isNonProfit) { this.isNonProfit = isNonProfit; }
}