package com.TeamVisibility.App.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Report entity (abuse / spam reports).
 *
 * Merged from feature/meeting-object. The original enums target_type and
 * status used lowercase names that collided with the JPA column called
 * status, which was technically legal but produced a confusing
 * "status status" line in the field declarations. The enums are renamed
 * to TargetType and ReportStatus to follow Java conventions; the database
 * column is still called status.
 */
@Entity
@Table(name = "reports")
public class Report {

    public enum TargetType {
        USER, EVENT
    }

    public enum ReportStatus {
        OPEN, CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reporterId;

    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    private Long targetId;

    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReportStatus status = ReportStatus.OPEN;

    public Report() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getReporterId() { return reporterId; }
    public void setReporterId(Long reporterId) { this.reporterId = reporterId; }

    public TargetType getTargetType() { return targetType; }
    public void setTargetType(TargetType targetType) { this.targetType = targetType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }
}
