package com.example.hospitalmanage.dao;

import com.example.hospitalmanage.model.chat.ChatRoom;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomDao {

    public Optional<String> getChatId(Long senderId, Long recipientId);

    ChatRoom findBySenderIdAndRecipientId(Long senderId, Long recipientId);

    void save(ChatRoom chatRoom);

    List<ChatRoom> findListChatRoomByUserId(Long id);
}
