package com.TeamVisibility.App.controller;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TeamVisibility.App.model.ChatMessage;
import com.TeamVisibility.App.model.User;
import com.TeamVisibility.App.service.ChatMessageService;
import com.TeamVisibility.App.repository.UserRepository;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;

    public ChatController(ChatMessageService cms, UserRepository ur) {
        this.chatMessageService = cms;
        this.userRepository = ur;
    }

    @GetMapping("/conversations")
    public ResponseEntity<?> conversations(HttpSession session) {
        Long uid = (Long) session.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<ChatMessage> all = chatMessageService.getAll();
        List<Map<String, Object>> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        // Private conversations
        Set<Long> partners = new HashSet<>();
        for (ChatMessage m : all) {
            if (m.getGroupEventId() != null) continue;
            if (m.getSenderId().equals(uid)) partners.add(m.getRecipientId());
            else if (uid.equals(m.getRecipientId())) partners.add(m.getSenderId());
        }
        for (Long pid : partners) {
            if (pid == null) continue;
            String key = "private-" + pid;
            if (seen.contains(key)) continue;
            seen.add(key);
            User partner = userRepository.findById(pid).orElse(null);
            String lastMsg = all.stream()
                .filter(m -> m.getGroupEventId() == null &&
                    ((m.getSenderId().equals(uid) && pid.equals(m.getRecipientId())) ||
                     (m.getSenderId().equals(pid) && uid.equals(m.getRecipientId()))))
                .reduce((a, b) -> b).map(ChatMessage::getContent).orElse("");
            Map<String, Object> conv = new LinkedHashMap<>();
            conv.put("type", "private");
            conv.put("recipientId", pid);
            conv.put("recipientName", partner != null ? partner.getFirstName() + " " + partner.getLastName() : "User");
            conv.put("recipientAvatar", partner != null ? partner.getAvatar() : null);
            conv.put("lastMessage", lastMsg.length() > 50 ? lastMsg.substring(0, 50) + "…" : lastMsg);
            result.add(conv);
        }

        // Group conversations
        Set<String> groups = new HashSet<>();
        for (ChatMessage m : all) {
            if (m.getGroupEventId() != null) groups.add(m.getGroupEventId());
        }
        for (String gid : groups) {
            boolean involved = all.stream().anyMatch(m ->
                gid.equals(m.getGroupEventId()) && m.getSenderId().equals(uid));
            // Show group if user sent a message there OR all groups
            String key = "group-" + gid;
            if (seen.contains(key)) continue;
            seen.add(key);
            String lastMsg = all.stream()
                .filter(m -> gid.equals(m.getGroupEventId()))
                .reduce((a, b) -> b).map(ChatMessage::getContent).orElse("");
            String groupName = all.stream()
                .filter(m -> gid.equals(m.getGroupEventId()) && m.getGroupName() != null)
                .findFirst().map(ChatMessage::getGroupName).orElse("Gruppe");
            Map<String, Object> conv = new LinkedHashMap<>();
            conv.put("type", "group");
            conv.put("groupEventId", gid);
            conv.put("groupName", groupName);
            conv.put("lastMessage", lastMsg.length() > 50 ? lastMsg.substring(0, 50) + "…" : lastMsg);
            result.add(conv);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/messages")
    public ResponseEntity<?> messages(
            @RequestParam(required = false) Long recipientId,
            @RequestParam(required = false) String groupEventId,
            HttpSession session) {
        Long uid = (Long) session.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<ChatMessage> all = chatMessageService.getAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (ChatMessage m : all) {
            boolean match;
            if (groupEventId != null && !groupEventId.isEmpty()) {
                match = groupEventId.equals(m.getGroupEventId());
            } else if (recipientId != null) {
                match = (m.getSenderId().equals(uid) && recipientId.equals(m.getRecipientId())) ||
                        (m.getSenderId().equals(recipientId) && uid.equals(m.getRecipientId()));
            } else continue;

            if (match) {
                User sender = userRepository.findById(m.getSenderId()).orElse(null);
                Map<String, Object> msg = new LinkedHashMap<>();
                msg.put("id", m.getId());
                msg.put("content", m.getContent());
                msg.put("mine", m.getSenderId().equals(uid));
                msg.put("senderName", sender != null ? sender.getFirstName() + " " + sender.getLastName() : "User");
                msg.put("senderAvatar", sender != null ? sender.getAvatar() : null);
                msg.put("createdAt", m.getCreatedAt());
                result.add(msg);
            }
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/messages")
    public ResponseEntity<?> send(@RequestBody Map<String, Object> body, HttpSession session) {
        Long uid = (Long) session.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String content = (String) body.get("content");
        if (content == null || content.trim().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Nachricht darf nicht leer sein"));

        ChatMessage msg = new ChatMessage();
        msg.setSenderId(uid);
        msg.setContent(content.trim());
        msg.setCreatedAt(LocalDateTime.now());

        if (body.containsKey("recipientId") && body.get("recipientId") != null) {
            msg.setRecipientId(((Number) body.get("recipientId")).longValue());
        }
        if (body.containsKey("groupEventId") && body.get("groupEventId") != null) {
            msg.setGroupEventId((String) body.get("groupEventId"));
        }
        if (body.containsKey("groupName") && body.get("groupName") != null) {
            msg.setGroupName((String) body.get("groupName"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(chatMessageService.save(msg));
    }
}