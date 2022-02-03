package com.example.hospitalmanage.service.impl;

import com.example.hospitalmanage.dao.ChatRoomDao;
import com.example.hospitalmanage.model.chat.ChatRoom;
import com.example.hospitalmanage.service.ChatRoomService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomDao chatRoomDao;
    @Override
    public List<ChatRoom> findListChatRoomByUserId(Long id) {
        return chatRoomDao.findListChatRoomByUserId(id);
    }
}
