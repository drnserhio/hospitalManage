package com.example.hospitalmanage.dao.impl;

import com.example.hospitalmanage.dao.ChatMessageDao;
import com.example.hospitalmanage.dao.ChatRoomDao;
import com.example.hospitalmanage.exception.domain.MessageNotFoundException;
import com.example.hospitalmanage.model.ChatMessage;
import com.example.hospitalmanage.model.MessageStatus;
import com.example.hospitalmanage.repos.ChatMessageRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.*;

import java.util.*;

import static com.example.hospitalmanage.constant.LoggerConstant.*;

@Repository
@Transactional
public class ChatMessageDaoImpl implements ChatMessageDao {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomDao chatRoomDao;
    private final MongoOperations mongoOperations;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ChatMessageDaoImpl(ChatMessageRepository chatMessageRepository,
                              ChatRoomDao chatRoomDao,
                              MongoOperations mongoOperations) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomDao = chatRoomDao;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        if (chatMessage.getContent().length() > 25) {
            chatMessage.setContent(convertContent(chatMessage.getContent()));
        }
        chatMessageRepository.save(chatMessage);
        LOGGER.info(SAVE_MESSAGE_SUCCESSFUL_IN_CHAT_ID + chatMessage.getChatId());
        return chatMessage;
    }

    private String convertContent(String content) {
        StringBuilder stringBuilder = new StringBuilder(content);
        int length = stringBuilder.length() / 25;
        int i = 25;
        while (length > 0) {
            stringBuilder.insert(i, "\n");
            length--;
            i += 25;
        }
        return stringBuilder.toString();
    }

    @Override
    public long countNewMessage(Long senderId, Long recipientId) {
        return chatMessageRepository
                .countBySenderIdAndRecipientIdAndStatus(senderId, recipientId, MessageStatus.RECEIVED);
    }

    public List<ChatMessage> findChatMessages(Long senderId, Long recipientId) {
        Optional<String> chatId = chatRoomDao.getChatId(senderId, recipientId, false);

        List<ChatMessage> messages =
                chatId
                        .map(id ->
                                chatMessageRepository.findByChatId(id)).orElse(new ArrayList<>());

        if (messages.size() > 0) {
            updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
        }
        LOGGER.info(FOUND_MESSAGES_WITH_SENDER + senderId + AND_RECIPIENT + recipientId);
        return messages;
    }

    public ChatMessage findById(String id) {
        return
                chatMessageRepository.findById(id)
                        .map(chatMsg -> {
                            chatMsg.setStatus(MessageStatus.DELIVERED);
                            return chatMessageRepository.save(chatMsg);
                        })
                        .orElseThrow(() ->
                                new MessageNotFoundException("can't find messages (" + id + ")"));
    }

    private void updateStatuses(Long senderId, Long recipientId, MessageStatus status) {
        Query query = new Query(
                Criteria
                        .where("senderId").is(senderId)
                        .and("recipientId").is(recipientId));
        Update update = Update.update("status", status);
        mongoOperations.updateMulti(query, update, ChatMessage.class);
        LOGGER.info(UPDATE_STATUS_MESSAGE);
    }
}
