package com.example.hospitalmanage.repos;

import com.example.hospitalmanage.model.chat.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {


    Optional<ChatRoom> findBySenderIdAndRecipientId(Long senderId, Long recipietnId);
}
