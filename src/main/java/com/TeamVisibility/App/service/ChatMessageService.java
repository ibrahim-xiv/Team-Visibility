package com.TeamVisibility.App.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.TeamVisibility.App.model.ChatMessage;
import com.TeamVisibility.App.repository.ChatMessageRepository;

@Service
public class ChatMessageService {
    private final ChatMessageRepository repo;
    public ChatMessageService(ChatMessageRepository repo) { this.repo = repo; }

    public List<ChatMessage> getAll() { return repo.findAllByOrderByCreatedAtAsc(); }
    public ChatMessage save(ChatMessage msg) { return repo.save(msg); }
}
