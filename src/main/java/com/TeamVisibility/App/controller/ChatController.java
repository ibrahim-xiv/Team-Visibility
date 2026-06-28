package com.TeamVisibility.App.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TeamVisibility.App.dto.ApiMessageResponse;
import com.TeamVisibility.App.dto.ChatConversationResponse;
import com.TeamVisibility.App.dto.ChatMessageRequest;
import com.TeamVisibility.App.dto.ChatMessageResponse;
import com.TeamVisibility.App.model.ChatMessage;
import com.TeamVisibility.App.service.ChatMessageService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final String SESSION_USER_ID = "userId";

    private final ChatMessageService chatMessageService;

    public ChatController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ChatConversationResponse>> listConversations(HttpSession session) {
        Long currentUserId = currentUserId(session);
        if (currentUserId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(chatMessageService.findConversations(currentUserId));
    }

    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageResponse>> listMessages(
        @RequestParam(required = false) Long recipientId,
        @RequestParam(required = false) String groupEventId,
        HttpSession session
    ) {
        Long currentUserId = currentUserId(session);
        if (currentUserId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(chatMessageService.findRecentMessages(currentUserId, recipientId, groupEventId).stream()
            .map(message -> ChatMessageResponse.from(message, currentUserId)).toList());
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> createMessage(@RequestBody ChatMessageRequest request, HttpSession session) {
        Long currentUserId = currentUserId(session);
        if (currentUserId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        ChatMessage message = chatMessageService.createMessage(request, currentUserId);
        return ResponseEntity.ok(ChatMessageResponse.from(message, currentUserId, true));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiMessageResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new ApiMessageResponse(exception.getMessage()));
    }

    private Long currentUserId(HttpSession session) {
        Object userId = session.getAttribute(SESSION_USER_ID);
        return userId instanceof Long id ? id : null;
    }
}