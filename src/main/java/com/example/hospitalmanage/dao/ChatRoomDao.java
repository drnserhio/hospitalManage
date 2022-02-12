package com.example.hospitalmanage.dao;

import com.example.hospitalmanage.model.chat.ChatRoom;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomDao {

    Optional<String> getChatId(Long senderId, Long recipientId, boolean createIfNotExist);
}
