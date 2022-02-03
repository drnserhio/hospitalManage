package com.example.hospitalmanage.dao.impl;

import com.example.hospitalmanage.dao.ChatRoomDao;
import com.example.hospitalmanage.dao.UserDao;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.chat.ChatRoom;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class ChatRoomDaoImpl implements ChatRoomDao {

    private final EntityManager entityManager;
    private final UserDao userDao;


    @Override
    public Optional<String> getChatId(
            Long senderId, Long recipientId) {

        ChatRoom sender = findBySenderIdAndRecipientId(senderId, recipientId);

        ChatRoom recipient = findBySenderIdAndRecipientId(recipientId, senderId);
        if (sender == null && recipient == null) {
            ChatRoom room = createRoom(senderId, recipientId);
            save(room);
            addChatromToUserId(Long.valueOf(senderId), room.getId());
            return Optional.of(room.getChatId());
        }
        if (sender == null) {
            if (!userIdWIthChatRooIdIsSave(senderId, recipient.getId())) {
                addChatromToUserId(senderId, recipient.getId());
            }
            return Optional.of(recipient.getChatId());
        }
        if (recipient == null) {
            if (!userIdWIthChatRooIdIsSave(recipientId, sender.getId())) {
                addChatromToUserId(recipientId, sender.getId());
            }
            return Optional.of(sender.getChatId());
        }
        return Optional.empty();
    }

    @Override
    public ChatRoom findBySenderIdAndRecipientId(Long senderId, Long recipientId) {
        ChatRoom chatRoom = null;
        try {
            Query query = entityManager
                    .createQuery("select chat from ChatRoom chat where chat.senderId = :senderId and chat.recipientId = :recipientId")
                    .setParameter("senderId", senderId)
                    .setParameter("recipientId", recipientId);
           chatRoom = (ChatRoom) query.getResultList().get(0);
        } catch (Exception e) {}
        return chatRoom;
    }

    @Override
    public void save(ChatRoom chatRoom) {
        try {
            entityManager
                    .persist(chatRoom);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ChatRoom> findListChatRoomByUserId(Long id) {
        List<ChatRoom> chatRooms = new ArrayList<>();
        try {
            Query query = entityManager
                    .createNativeQuery("select distinct * from chat_room room where room.sender_id = :id or room.recipient_id = :id")
                    .setParameter("id", id)
                    .setParameter("id", id);
            chatRooms = (List<ChatRoom>) query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatRooms;
    }

    private boolean userIdWIthChatRooIdIsSave(Long userId, Long chatRoomId) {
        Query nativeQuery = entityManager
                .createNativeQuery("select count(users_chat_rooms.user_id) from users_chat_rooms where user_id = :userId and chat_room_id = :chatRoomId")
                .setParameter("userId", userId)
                .setParameter("chatRoomId", chatRoomId);
        return ((BigInteger) nativeQuery.getSingleResult()).longValue() == 1;
    }

    private ChatRoom createRoom(Long senderId, Long recipientId) {
        User sender = userDao.findUserByUserId(senderId);
        User recipient = userDao.findUserByUserId(recipientId);

        String chatId = generateChatId(sender.getUsername(), recipient.getUsername());

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setChatId(chatId);
        chatRoom.setSenderId(senderId);
        chatRoom.setRecipientId(recipientId);
        return chatRoom;
    }

    private String generateChatId(String senderId, String recipientId) {
        String genarate = UUID.randomUUID().toString().substring(0, 4) + String.valueOf(senderId + recipientId).hashCode() * 31;
        return genarate;
    }

    public void addChatromToUserId(Long userId, Long chatRoomId) {
        try {
            entityManager
                    .createNativeQuery("insert users_chat_rooms values (:userId, :chatRoomId)")
                    .setParameter("userId", userId)
                    .setParameter("chatRoomId", chatRoomId)
                    .executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
