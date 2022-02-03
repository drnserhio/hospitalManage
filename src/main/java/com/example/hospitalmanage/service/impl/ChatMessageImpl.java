package com.example.hospitalmanage.service.impl;

import com.example.hospitalmanage.dao.ChatMessageDao;
import com.example.hospitalmanage.model.chat.ChatMessage;
import com.example.hospitalmanage.model.chat.Message;
import com.example.hospitalmanage.model.chat.MessageStatus;
import com.example.hospitalmanage.service.ChatMessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ChatMessageImpl implements ChatMessageService {

    private final ChatMessageDao chatMessageDao;


    @Override
    public long countNewMessages(Long senderId, Long recipientId) {
        return chatMessageDao.countNewMessages(senderId, recipientId);
    }

    @Override
    public List<ChatMessage> findByChatId(String chatId) {
        return chatMessageDao.findByChatId(chatId);
    }

    @Override
    public List<ChatMessage> findChatMessages(Long senderId, Long recipientId) {
        return chatMessageDao.findChatMessages(senderId, recipientId);
    }

    @Override
    public void sendMessage(Long senderId, Long recipientId, Message msg) {
        chatMessageDao.sendMessage(senderId, recipientId, msg);
    }
}
