package com.TeamVisibility.App.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TeamVisibility.App.model.Report;
import com.TeamVisibility.App.service.ReportService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Steve: Report Button BE + FE verbinden
    @PostMapping
    public ResponseEntity<?> createReport(@RequestBody Report report) {
        try {
            Report saved = reportService.createReport(report);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Steve: Alle Reports laden (Admin-Ansicht)
    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportService.findAll());
    }
}
