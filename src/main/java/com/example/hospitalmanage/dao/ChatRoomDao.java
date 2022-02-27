package com.example.hospitalmanage.dao;

import java.util.Optional;

public interface ChatRoomDao {

    Optional<String> getChatId(Long senderId, Long recipientId, boolean createIfNotExist);
}
