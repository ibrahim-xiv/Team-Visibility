package com.TeamVisibility.App.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Report {
    enum target_type{
        USER,
        EVENT
    }
    enum status{
        OPEN,
        CLOSED
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reporter_id;
    private target_type targetType;
    private Long target_id;
    private String reason;
    private status status;


    //Leerer Konstruktor
    public Report() {}


    //Getter / Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getReporter_id() {
        return reporter_id;
    }
    public void setReporter_id(Long reporter_id) {
        this.reporter_id = reporter_id;
    }
    public target_type getTargetType() {
        return targetType;
    }
    public void setTargetType(target_type targetType) {
        this.targetType = targetType;
    }
    public Long getTarget_id() {
        return target_id;
    }
    public void setTarget_id(Long target_id) {
        this.target_id = target_id;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public status getStatus() {
        return status;
    }
    public void setStatus(status status) {
        this.status = status;
    }

    
}
