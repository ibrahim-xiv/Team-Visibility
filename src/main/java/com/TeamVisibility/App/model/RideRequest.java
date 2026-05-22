package com.TeamVisibility.App.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class RideRequest{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long event_id;
    private long requester_id;
    private long provider_id;

    

    public RideRequest() {
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getEvent_id() {
        return event_id;
    }
    public void setEvent_id(long event_id) {
        this.event_id = event_id;
    }
    public long getRequester_id() {
        return requester_id;
    }
    public void setRequester_id(long requester_id) {
        this.requester_id = requester_id;
    }
    public long getProvider_id() {
        return provider_id;
    }
    public void setProvider_id(long provider_id) {
        this.provider_id = provider_id;
    }
    

}