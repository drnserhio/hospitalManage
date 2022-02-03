package com.example.hospitalmanage.service;

import com.example.hospitalmanage.model.chat.ChatRoom;

import java.util.List;

public interface ChatRoomService {

    List<ChatRoom> findListChatRoomByUserId(Long id);
}
