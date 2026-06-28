package com.TeamVisibility.App.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.TeamVisibility.App.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndReadFalse(Long userId);
}
