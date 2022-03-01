package com.example.hospitalmanage.dao.impl;

import com.example.hospitalmanage.dao.ChatRoomDao;
import com.example.hospitalmanage.model.ChatRoom;
import com.example.hospitalmanage.repos.ChatRoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ChatRoomDaoImpl implements ChatRoomDao {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomDaoImpl(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    @Override
    public Optional<String> getChatId(Long senderId, Long recipientId, boolean createIfNotExist) {

        Optional<ChatRoom> senderRoom = chatRoomRepository
                .findBySenderIdAndRecipientId(senderId, recipientId);
        Optional<ChatRoom> recipientRoom = chatRoomRepository
                .findBySenderIdAndRecipientId(recipientId, senderId);

        if (senderRoom.isEmpty() && recipientRoom.isEmpty()) {
            var chatId =
                    String.format("%s_%s", senderId, recipientId);

            ChatRoom senderRecipient = ChatRoom
                    .builder()
                    .chatId(chatId)
                    .senderId(senderId)
                    .recipientId(recipientId)
                    .build();

            chatRoomRepository.save(senderRecipient);
            return Optional.of(chatId);
        }
        if (senderRoom.isPresent()) {
            return Optional.of(senderRoom.get().getChatId());
        }

        if (recipientRoom.isPresent()) {
            return Optional.of(recipientRoom.get().getChatId());
        }
        return Optional.empty();
    }
}
