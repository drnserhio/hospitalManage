package com.example.hospitalmanage.service;


import com.example.hospitalmanage.model.ChatMessage;
import com.example.hospitalmanage.model.MessageStatus;
import com.example.hospitalmanage.model.User;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.example.hospitalmanage.constant.InitialDataConst.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChatRoomServiceTest {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private UserService userService;


    @Test
    @Order(1)
    public void shouldCreateChatRoomWithSenderAndRecipient() {
        User rick = userService.findUserByUsername(USERNAME_RICK);
        User morthy = userService.findUserByUsername(USERNAME_MORTHY);

        Optional<String> senderChatId = chatRoomService.getChatId(rick.getId(), morthy.getId(), true);
        List<ChatMessage> chatMessages = chatMessageService.findChatMessages(rick.getId(), morthy.getId());
        Optional<String> recipientChatId = chatRoomService.getChatId(morthy.getId(), rick.getId(), true);
        assertNotNull(senderChatId);
        assertNotNull(recipientChatId);
        assertNotNull(chatMessages);
    }

    @Test
    @Order(2)
    public void shouldWhenUserSendMessage() {
        User rick = userService.findUserByUsername(USERNAME_RICK);
        User morthy = userService.findUserByUsername(USERNAME_MORTHY);

        Optional<String> senderChatId = chatRoomService.getChatId(rick.getId(), morthy.getId(), true);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatId(senderChatId.get());
        chatMessage.setSenderId(rick.getId());
        chatMessage.setRecipientId(morthy.getId());
        chatMessage.setSenderName(rick.getUsername());
        chatMessage.setRecipientName(morthy.getUsername());
        chatMessage.setContent("Hello it's test!");
        chatMessage.setTimestamp(new Date());
        chatMessage.setStatus(MessageStatus.DELIVERED);
        ChatMessage save = chatMessageService.save(chatMessage);
        List<ChatMessage> chatMessages = chatMessageService.findChatMessages(rick.getId(), morthy.getId());
        assertNotNull(save);
        assertEquals(1, chatMessages.size());
        assertEquals(save.getChatId(), chatMessages.get(0).getChatId());
        assertEquals(chatMessages.get(0). getSenderId(), rick.getId());
        assertEquals(chatMessages.get(0). getRecipientId(), morthy.getId());
    }
}
