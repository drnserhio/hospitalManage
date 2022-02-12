package com.example.hospitalmanage.repos;

import com.example.hospitalmanage.model.chat.ChatMessage;
import com.example.hospitalmanage.model.chat.MessageStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository
        extends MongoRepository<ChatMessage, String> {

    long countBySenderIdAndRecipientIdAndStatus(
            Long senderId, Long recipientId, MessageStatus status);

    List<ChatMessage> findByChatId(String chatId);
}
