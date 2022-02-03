package com.example.hospitalmanage.service;

import com.example.hospitalmanage.model.chat.ChatMessage;
import com.example.hospitalmanage.model.chat.Message;
import com.example.hospitalmanage.model.chat.MessageStatus;

import java.util.List;

public interface ChatMessageService {

    long countNewMessages(Long senderId, Long recipientId) ;

    List<ChatMessage> findByChatId(String chatId);

    List<ChatMessage> findChatMessages(Long senderId, Long recipientId);

    void sendMessage(Long senderId, Long recipientId, Message msg) ;
}
