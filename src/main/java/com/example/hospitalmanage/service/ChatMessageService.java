package com.example.hospitalmanage.service;

import com.example.hospitalmanage.model.chat.ChatMessage;
import com.example.hospitalmanage.model.chat.Message;
import com.example.hospitalmanage.model.chat.MessageStatus;

import java.util.List;

public interface ChatMessageService {

    ChatMessage save(ChatMessage chatMessage);

    long countNewMessage(Long senderId, Long recipientId);

    List<ChatMessage> findChatMessages(Long senderId, Long recipientId);

    ChatMessage findById(String id);
}
