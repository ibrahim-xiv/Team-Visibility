package com.TeamVisibility.App.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.TeamVisibility.App.model.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByOrderByCreatedAtAsc();
}
