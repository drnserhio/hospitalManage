package com.example.hospitalmanage.service;

import com.example.hospitalmanage.model.chat.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomService {

    Optional<String> getChatId(Long senderId, Long recipientId, boolean createIfNotExist);
}
