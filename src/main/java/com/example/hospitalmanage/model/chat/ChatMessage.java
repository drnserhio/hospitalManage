package com.example.hospitalmanage.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
   @Id
   private String id;
   private String chatId;
   private Long senderId;
   private Long recipientId;
   private String senderName;
   private String recipientName;
   private String content;
   private Date timestamp;
   private MessageStatus status;
}
