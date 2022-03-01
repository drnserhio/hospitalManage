package com.example.hospitalmanage.resource;

import com.example.hospitalmanage.exception.ExceptionHandling;
import com.example.hospitalmanage.model.ChatMessage;
import com.example.hospitalmanage.model.ChatNotification;
import com.example.hospitalmanage.service.ChatMessageService;
import com.example.hospitalmanage.service.ChatRoomService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

@RestController
@Slf4j
public class ChatResource extends ExceptionHandling {

    private final SimpMessagingTemplate simpleMessagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    public ChatResource(SimpMessagingTemplate simpleMessagingTemplate,
                        ChatMessageService chatMessageService,
                        ChatRoomService chatRoomService) {
        this.simpleMessagingTemplate = simpleMessagingTemplate;
        this.chatMessageService = chatMessageService;
        this.chatRoomService = chatRoomService;
    }

    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        Optional<String> chatId = chatRoomService
                .getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true);
        chatMessage.setChatId(chatId.get());

        ChatMessage save = chatMessageService.save(chatMessage);
        simpleMessagingTemplate.convertAndSendToUser(
                String.valueOf(chatMessage.getRecipientId()), "/queue/messages",
                new ChatNotification(
                        save.getId(),
                        save.getSenderId(),
                        save.getSenderName()
                )
        );

    }

    @GetMapping("/messages/{senderId}/{recipientId}/count")
    public ResponseEntity<Long> countNewMessages(
            @PathVariable Long senderId,
            @PathVariable Long recipientId) {
        long countNewMessage = chatMessageService.countNewMessage(senderId, recipientId);
        return new ResponseEntity<>(countNewMessage, OK);
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<?> findChatMessages(
            @PathVariable Long senderId,
            @PathVariable Long recipientId) {
        List<ChatMessage> chatMessages = chatMessageService.findChatMessages(senderId, recipientId);
        return new ResponseEntity<>(chatMessages, OK);
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<?> findMessages(
            @PathVariable String id) {
        ChatMessage chatMessage = chatMessageService.findById(id);
        return new ResponseEntity<>(chatMessage, OK);
    }



}
