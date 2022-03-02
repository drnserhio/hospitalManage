package com.example.hospitalmanage.dao.impl;

import com.example.hospitalmanage.dao.ChatRoomDao;
import com.example.hospitalmanage.model.ChatRoom;
import com.example.hospitalmanage.repos.ChatRoomRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ChatRoomDaoImpl implements ChatRoomDao {

    private final ChatRoomRepository chatRoomRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

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
            LOGGER.info("Create chat room with id: " + senderRecipient.getChatId());
            return Optional.of(chatId);
        }
        if (senderRoom.isPresent()) {
            LOGGER.info("Find chat room with id: " + senderRoom.get().getChatId());
            return Optional.of(senderRoom.get().getChatId());
        }

        if (recipientRoom.isPresent()) {
            LOGGER.info("Find chat room with id: " + recipientRoom.get().getChatId());
            return Optional.of(recipientRoom.get().getChatId());
        }
        return Optional.empty();
    }
}
