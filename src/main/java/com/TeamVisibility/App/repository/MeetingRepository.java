package com.TeamVisibility.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TeamVisibility.App.model.Meeting;
import java.util.List;
@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

// Steve: Für Kategorie-Filter (Fenster 2)
    // Spring Data kann "category_id" nicht automatisch parsen, daher @Query
    @Query("SELECT m FROM Meeting m WHERE m.category_id = :categoryId")
    List<Meeting> findByCategoryId(@Param("categoryId") Long categoryId);
}