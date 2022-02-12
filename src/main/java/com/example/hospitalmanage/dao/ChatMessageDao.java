package com.example.hospitalmanage.dao;

import com.example.hospitalmanage.model.chat.ChatMessage;
import com.example.hospitalmanage.model.chat.Message;
import com.example.hospitalmanage.model.chat.MessageStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageDao {

    ChatMessage save(ChatMessage chatMessage);

    long countNewMessage(Long senderId, Long recipientId);

    List<ChatMessage> findChatMessages(Long senderId, Long recipientId);

    ChatMessage findById(String id);
}
