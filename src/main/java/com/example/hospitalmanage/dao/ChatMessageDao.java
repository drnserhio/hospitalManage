package com.example.hospitalmanage.dao;

import com.example.hospitalmanage.model.chat.ChatMessage;
import com.example.hospitalmanage.model.chat.Message;
import com.example.hospitalmanage.model.chat.MessageStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageDao {


     long countNewMessages(Long senderId, Long recipientId) ;

    List<ChatMessage> findByChatId(String chatId);
    void sendMessage(Long senderId, Long recipientId, Message msg) ;

    ChatMessage findChatMessageById(Long id) ;

     List<ChatMessage> findChatMessages(Long senderId, Long recipientId);
}
