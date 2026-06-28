package com.TeamVisibility.App.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TeamVisibility.App.model.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("""
        select message from ChatMessage message
        where message.conversationType = 'PRIVATE' and ((
            message.sender.id = :currentUserId and message.recipient.id = :recipientId
        ) or (
            message.sender.id = :recipientId and message.recipient.id = :currentUserId
        ))
        order by message.createdAt desc
        """)
    List<ChatMessage> findConversationMessages(@Param("currentUserId") Long currentUserId, @Param("recipientId") Long recipientId, Pageable pageable);

    @Query("""
        select message from ChatMessage message
        where message.conversationType = 'GROUP' and message.groupEventId = :groupEventId
        order by message.createdAt desc
        """)
    List<ChatMessage> findGroupMessages(@Param("groupEventId") String groupEventId, Pageable pageable);

    @Query("""
        select message from ChatMessage message
        where message.conversationType = 'PRIVATE' and (message.sender.id = :currentUserId or message.recipient.id = :currentUserId)
        order by message.createdAt desc
        """)
    List<ChatMessage> findPrivateConversations(@Param("currentUserId") Long currentUserId);

    @Query("""
        select message from ChatMessage message
        where message.conversationType = 'GROUP'
        order by message.createdAt desc
        """)
    List<ChatMessage> findGroupConversations();
}
