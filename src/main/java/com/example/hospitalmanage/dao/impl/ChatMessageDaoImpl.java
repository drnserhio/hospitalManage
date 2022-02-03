package com.example.hospitalmanage.dao.impl;

import com.example.hospitalmanage.dao.ChatMessageDao;
import com.example.hospitalmanage.dao.ChatRoomDao;
import com.example.hospitalmanage.dao.UserDao;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.chat.ChatMessage;
import com.example.hospitalmanage.model.chat.Message;
import com.example.hospitalmanage.model.chat.MessageStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

@Repository
@AllArgsConstructor
@Transactional
public class ChatMessageDaoImpl implements ChatMessageDao {

    private final EntityManager entityManager;
    private final UserDao userDao;
    private final ChatRoomDao chatRoomDao;

    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        try {
            entityManager
                    .persist(chatMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessage;
    }


    @Override
    public List<ChatMessage> findChatMessages(Long senderId, Long recipientId) {
        var chatId = chatRoomDao.getChatId(senderId, recipientId);

        var messages =
                chatId.map(cId -> findByChatId(cId)).orElse(new ArrayList<>());

        if(messages.size() > 0) {
            updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
        }
        return messages;
    }

    public ChatMessage findChatMessageById(Long id) {
        ChatMessage chatMessage = null;
        try {
            Query query = entityManager
                    .createQuery("select chat from ChatMessage chat where chat.id = :id")
                    .setParameter("id", id);
            chatMessage = (ChatMessage) query.getResultList().get(0);
            chatMessage.setStatus(MessageStatus.DELIVERED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessage;
    }

    public void updateStatuses(Long senderId, Long recipientId, MessageStatus status) {

//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<ChatMessage> qy = cb.createQuery(ChatMessage.class);

//        Root<ChatMessage> root = qy.from(ChatMessage.class);
//        qy.select(root).where(cb.equal("sederId", senderId)).where(cb.equal("recipientId", recipientId))

//        Query query = new Query(
//                Criteria
//                        .where("senderId").is(senderId)
//                        .and("recipientId").is(recipientId));
//        Update update = Update.update("status", status);
//        mongoOperations.updateMulti(query, update, ChatMessage.class);
    }

    @Override
    public void sendMessage(Long senderId, Long recipientId, Message msg) {

        if (!isExistUser(senderId)
                || !isExistUser(recipientId)) {
            throw new RuntimeException("Users not have in base.");
        }
        String chatId = chatRoomDao.getChatId(senderId, recipientId).get();
        User sender = findUserById(senderId);
        User recipient = findUserById(recipientId);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatId(chatId);
        chatMessage.setSenderId(senderId);
        chatMessage.setRecipientId(recipientId);
        chatMessage.setSenderName(sender.getUsername());
        chatMessage.setRecipientName(recipient.getUsername());
        chatMessage.setContent(msg.getMsg());
        chatMessage.setTimestamp(new Date());
        chatMessage.setStatus(MessageStatus.DELIVERED);
        save(chatMessage);
    }

    private boolean isExistUser(Long id) {
        return userDao.isExistUser(id);
    }

    private User findUserById(Long id) {
        return userDao.findUserByUserId(id);
    }

    public long countNewMessages(Long senderId, Long recipientId) {
        return countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.RECEIVED);
    }

    public long countBySenderIdAndRecipientIdAndStatus(Long senderId, Long recipientId, MessageStatus status) {
        //TODO: count msg
        return 0;
    }

    @Override
    public List<ChatMessage> findByChatId(String chatId) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        try {
            Query query = entityManager.
                    createQuery("select message from ChatMessage message where message.chatId = :chatId")
                    .setParameter("chatId", chatId);
            chatMessages = (List<ChatMessage>) query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessages;
    }
}
