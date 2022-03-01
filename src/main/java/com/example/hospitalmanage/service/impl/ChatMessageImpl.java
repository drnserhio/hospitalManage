package com.example.hospitalmanage.service.impl;

import com.example.hospitalmanage.dao.ChatMessageDao;
import com.example.hospitalmanage.model.ChatMessage;
import com.example.hospitalmanage.service.ChatMessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageImpl implements ChatMessageService {

    private final ChatMessageDao chatMessageDao;

    public ChatMessageImpl(ChatMessageDao chatMessageDao) {
        this.chatMessageDao = chatMessageDao;
    }

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageDao.save(chatMessage);
    }

    @Override
    public long countNewMessage(Long senderId, Long recipientId) {
        return chatMessageDao.countNewMessage(senderId, recipientId);
    }

    @Override
    public List<ChatMessage> findChatMessages(Long senderId, Long recipientId) {
        return chatMessageDao.findChatMessages(senderId, recipientId);
    }

    @Override
    public ChatMessage findById(String id) {
        return chatMessageDao.findById(id);
    }
}
