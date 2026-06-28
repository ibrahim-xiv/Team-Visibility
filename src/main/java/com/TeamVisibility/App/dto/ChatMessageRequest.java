package com.TeamVisibility.App.dto;

public record ChatMessageRequest(String content, Long recipientId, String groupEventId, String groupName) {
}
