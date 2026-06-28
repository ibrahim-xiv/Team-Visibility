package com.TeamVisibility.App.dto;

import java.time.LocalDateTime;

import com.TeamVisibility.App.model.ChatMessage;
import com.TeamVisibility.App.model.User;

public record ChatMessageResponse(
    Long id,
    Long senderId,
    String senderName,
    String content,
    LocalDateTime createdAt,
    boolean mine
) {
    public static ChatMessageResponse from(ChatMessage message, Long currentUserId) {
        return from(message, currentUserId, false);
    }

    public static ChatMessageResponse from(ChatMessage message, Long currentUserId, boolean forceMine) {
        User sender = message.getSender();
        Long senderId = sender == null ? null : sender.getId();
        String senderName = sender == null ? "Gast" : sender.getFirstName() + " " + sender.getLastName();
        boolean mine = forceMine || (currentUserId != null && currentUserId.equals(senderId));

        return new ChatMessageResponse(
            message.getId(),
            senderId,
            senderName,
            message.getContent(),
            message.getCreatedAt(),
            mine
        );
    }
}
