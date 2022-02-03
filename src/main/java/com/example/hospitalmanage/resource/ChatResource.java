package com.example.hospitalmanage.resource;

import com.example.hospitalmanage.exception.ExceptionHandling;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.chat.ChatMessage;
import com.example.hospitalmanage.model.chat.ChatRoom;
import com.example.hospitalmanage.model.chat.Message;
import com.example.hospitalmanage.service.ChatMessageService;
import com.example.hospitalmanage.service.ChatRoomService;
import com.example.hospitalmanage.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = {"/", "/chat"})
public class ChatResource extends ExceptionHandling {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final UserService userService;


    @GetMapping("/messages/{senderId}/{recipientId}/count")
    @PreAuthorize("hasAnyAuthority('god:all', 'profile:user', 'profile:all')")
    public ResponseEntity<Long> countNewMessages(
            @PathVariable Long senderId,
            @PathVariable Long recipientId) {
        long count = chatMessageService.countNewMessages(senderId, recipientId);
        return new ResponseEntity<>(count, OK);
    }

    @GetMapping("/find/chat/messages/{senderId}/{recipientId}")
    @PreAuthorize("hasAnyAuthority('god:all', 'profile:user', 'profile:all')")
    public ResponseEntity<List<ChatMessage>> findChatMessages (
            @PathVariable Long senderId,
            @PathVariable Long recipientId) {
        List<ChatMessage> chatMessages = chatMessageService.findChatMessages(senderId, recipientId);
        return new ResponseEntity<>(chatMessages, OK);
    }

    @PostMapping("/send/messages/{senderId}/{recipientId}")
    @PreAuthorize("hasAnyAuthority('god:all', 'profile:user', 'profile:all')")
    public void sendMessages(
            @PathVariable Long senderId,
            @PathVariable Long recipientId,
            @RequestBody Message msg) {
        chatMessageService.sendMessage(senderId, recipientId, msg);
    }


    @GetMapping("/find/chatRoom/{userId}")
    @PreAuthorize("hasAnyAuthority('god:all', 'profile:user', 'profile:all')")
    public ResponseEntity<List<ChatRoom>> findListChatByUserId(
            @PathVariable Long userId) {
        List<ChatRoom> chatRooms = chatRoomService.findListChatRoomByUserId(userId);
        return new ResponseEntity<>(chatRooms, OK);
    }

    @GetMapping("/find/user/{userId}")
    @PreAuthorize("hasAnyAuthority('god:all', 'profile:user', 'profile:all')")
    public ResponseEntity<User> findUserByUserId(
            @PathVariable Long userId) {
        User user = userService.findUserByUserId(userId);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/list-users/{userId}")
    @PreAuthorize("hasAnyAuthority('god:all', 'profile:user', 'profile:all')")
    public ResponseEntity<List<User>> findAllChatUsersByUserId(
            @PathVariable Long userId) {
        List<User> users = userService.findAllChatUsersByUserId(userId);
        return new ResponseEntity<>(users, OK);
    }
}
