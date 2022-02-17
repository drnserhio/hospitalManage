package com.example.hospitalmanage.repos;

import com.example.hospitalmanage.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {


    Optional<ChatRoom> findBySenderIdAndRecipientId(Long senderId, Long recipietnId);
}
