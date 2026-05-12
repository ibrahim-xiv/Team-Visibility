package com.TeamVisibility.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TeamVisibility.App.model.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
}
