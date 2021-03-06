package com.example.hospitalmanage.service.impl;

import com.example.hospitalmanage.dao.ChatRoomDao;
import com.example.hospitalmanage.service.ChatRoomService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomDao chatRoomDao;

    public ChatRoomServiceImpl(ChatRoomDao chatRoomDao) {
        this.chatRoomDao = chatRoomDao;
    }

    @Override
    public Optional<String> getChatId(Long senderId, Long recipientId, boolean createIfNotExist) {
        return chatRoomDao.getChatId(senderId, recipientId, createIfNotExist);
    }
}
