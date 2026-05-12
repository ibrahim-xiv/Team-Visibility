package com.TeamVisibility.App.service;

import org.springframework.stereotype.Service;

import com.TeamVisibility.App.model.Report;
import com.TeamVisibility.App.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Report createReport(Report report) {
        return reportRepository.save(report);
    }
}
