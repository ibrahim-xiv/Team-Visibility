package com.TeamVisibility.App.service;

import org.springframework.stereotype.Service;

import com.TeamVisibility.App.model.Report;
import com.TeamVisibility.App.repository.ReportRepository;
import java.util.List;
@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Report createReport(Report report) {
        return reportRepository.save(report);
    }
// Steve: Für Admin-Ansicht - alle Reports laden
public List<Report> findAll() {
    return reportRepository.findAll();
}
}
