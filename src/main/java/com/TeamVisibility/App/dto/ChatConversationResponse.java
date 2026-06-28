package com.TeamVisibility.App.dto;

import java.time.LocalDateTime;

public record ChatConversationResponse(
    String type,
    Long recipientId,
    String recipientName,
    String groupEventId,
    String groupName,
    String lastMessage,
    LocalDateTime lastMessageAt
) {
}
