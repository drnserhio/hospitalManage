package com.example.hospitalmanage.dao;

import com.example.hospitalmanage.model.ChatMessage;

import java.util.List;

public interface ChatMessageDao {

    ChatMessage save(ChatMessage chatMessage);

    long countNewMessage(Long senderId, Long recipientId);

    List<ChatMessage> findChatMessages(Long senderId, Long recipientId);

    ChatMessage findById(String id);
}
