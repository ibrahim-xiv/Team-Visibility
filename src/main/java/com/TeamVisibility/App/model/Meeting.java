package com.TeamVisibility.App.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Meeting / Event entity.
 *
 * Merged from:
 *   - feature/registration (base shape)
 *   - feature/meeting-object (creator/category/lat/lng/locationName/date_time)
 *   - feature/favorite (kept compatible field set)
 *
 * The simple Long foreign-key style (creator_id, category_id) is preserved
 * intentionally - the branches do not yet model proper JPA relations, and the
 * goal here is stabilization, not refactor.
 */
@Entity
@Table(name = "meetings")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long creatorId;
    private Long categoryId;

    private String title;
    private String description;

    /** Free-text location name shown in UI (e.g. "Stadtpark"). */
    private String locationName;

    /** Latitude / longitude for the map view. */
    private Float lat;
    private Float lng;

    private LocalDateTime dateTime;

    /** Branches enforced that meetings must be non-profit. Kept as a flag. */
    private Boolean nonProfit;

    private String category;

    public Meeting() {
    }

    // --- getters / setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public Float getLat() { return lat; }
    public void setLat(Float lat) { this.lat = lat; }

    public Float getLng() { return lng; }
    public void setLng(Float lng) { this.lng = lng; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public Boolean getNonProfit() { return nonProfit; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public void setNonProfit(Boolean nonProfit) { this.nonProfit = nonProfit; }
}
