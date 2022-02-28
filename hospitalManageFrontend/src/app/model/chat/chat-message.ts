export class ChatMessage {
  id?: string;
  chatId?: string;
  senderId?: number;
  recipientId?: number;
  senderName?: string;
  recipientName?: string;
  content?: string;
  timestamp?: Date;
  status?: string;
}
