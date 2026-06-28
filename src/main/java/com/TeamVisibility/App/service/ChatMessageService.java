package com.TeamVisibility.App.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TeamVisibility.App.dto.ChatConversationResponse;
import com.TeamVisibility.App.dto.ChatMessageRequest;
import com.TeamVisibility.App.model.ChatMessage;
import com.TeamVisibility.App.model.User;
import com.TeamVisibility.App.repository.ChatMessageRepository;
import com.TeamVisibility.App.repository.UserRepository;

@Service
public class ChatMessageService {

    private static final int MAX_MESSAGE_LENGTH = 2_000;
    private static final int CONVERSATION_MESSAGE_LIMIT = 50;

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> findRecentMessages(Long currentUserId, Long recipientId, String groupEventId) {
        requireCurrentUser(currentUserId);
        if (hasText(groupEventId)) {
            List<ChatMessage> messages = new ArrayList<>(chatMessageRepository.findGroupMessages(groupEventId.trim(), PageRequest.of(0, CONVERSATION_MESSAGE_LIMIT)));
            Collections.reverse(messages);
            return messages;
        }
        requireConversationParticipants(currentUserId, recipientId);
        ensureUserExists(recipientId, "Chat-Partner wurde nicht gefunden.");

        List<ChatMessage> messages = new ArrayList<>(chatMessageRepository.findConversationMessages(currentUserId, recipientId, PageRequest.of(0, CONVERSATION_MESSAGE_LIMIT)));
        Collections.reverse(messages);
        return messages;
    }

    @Transactional(readOnly = true)
    public List<ChatConversationResponse> findConversations(Long currentUserId) {
        requireCurrentUser(currentUserId);
        Map<String, ChatConversationResponse> conversations = new LinkedHashMap<>();

        for (ChatMessage message : chatMessageRepository.findPrivateConversations(currentUserId)) {
            User other = currentUserId.equals(message.getSender().getId()) ? message.getRecipient() : message.getSender();
            if (other == null) continue;
            String key = "private:" + other.getId();
            conversations.putIfAbsent(key, new ChatConversationResponse("private", other.getId(), displayName(other), null, null, message.getContent(), message.getCreatedAt()));
        }

        for (ChatMessage message : chatMessageRepository.findGroupConversations()) {
            if (!hasText(message.getGroupEventId())) continue;
            String key = "group:" + message.getGroupEventId();
            conversations.putIfAbsent(key, new ChatConversationResponse("group", null, null, message.getGroupEventId(), message.getGroupName(), message.getContent(), message.getCreatedAt()));
        }

        return new ArrayList<>(conversations.values());
    }

    @Transactional
    public ChatMessage createMessage(ChatMessageRequest request, Long senderId) {
        String content = requireContent(request == null ? null : request.content());
        String groupEventId = request == null ? null : request.groupEventId();
        User sender = ensureUserExists(senderId, "Benutzer wurde nicht gefunden.");

        ChatMessage message = new ChatMessage();
        message.setContent(content);
        message.setSender(sender);

        if (hasText(groupEventId)) {
            message.setConversationType("GROUP");
            message.setGroupEventId(groupEventId.trim());
            message.setGroupName(hasText(request.groupName()) ? request.groupName().trim() : "Gruppen-Chat");
        } else {
            Long recipientId = request == null ? null : request.recipientId();
            requireConversationParticipants(senderId, recipientId);
            message.setConversationType("PRIVATE");
            message.setRecipient(ensureUserExists(recipientId, "Chat-Partner wurde nicht gefunden."));
        }

        return chatMessageRepository.save(message);
    }

    private void requireCurrentUser(Long currentUserId) {
        if (currentUserId == null) throw new IllegalArgumentException("Bitte melde dich an, um den Chat zu nutzen.");
    }

    private void requireConversationParticipants(Long currentUserId, Long recipientId) {
        requireCurrentUser(currentUserId);
        if (recipientId == null) throw new IllegalArgumentException("Bitte wähle einen Chat-Partner aus.");
        if (currentUserId.equals(recipientId)) throw new IllegalArgumentException("Du kannst keinen Chat mit dir selbst öffnen.");
    }

    private User ensureUserExists(Long userId, String message) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(message));
    }

    private String displayName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String requireContent(String content) {
        if (content == null || content.trim().isEmpty()) throw new IllegalArgumentException("Nachricht darf nicht leer sein.");
        String trimmed = content.trim();
        if (trimmed.length() > MAX_MESSAGE_LENGTH) throw new IllegalArgumentException("Nachricht darf maximal 2000 Zeichen lang sein.");
        return trimmed;
    }
}
