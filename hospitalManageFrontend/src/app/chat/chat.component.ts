import {Component, OnInit} from '@angular/core';
import {ChatMessage} from '../model/chat/chat-message';
import {User} from '../model/user';
import {ChatService} from '../service/chat.service';
import {AuthService} from '../service/auth-service.service';
import {HttpErrorResponse} from '@angular/common/http';
import {NgForm} from '@angular/forms';
import {NotifyService} from '../service/notify-service.service';
import {NotifyType} from '../enum/notify-type.enum';
import {UserService} from '../service/user-service.service';
import * as SockJS from 'sockjs-client';
import {Stomp} from '@stomp/stompjs';


@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {

  chatMessages: ChatMessage[] = [];
  listUsers: User[] = [];

  sessionUser: User = null;
  selectUser: User = null;

  countNewMsg: number;
  flag = true;

  private ws;
  public stompClient;

  constructor(private authService: AuthService,
              private notify: NotifyService,
              private userService: UserService,
              private chatService: ChatService) {
    this.connection();
  }

  ngOnInit(): void {
    this.authSession();
    this.channels();
  }

  connection() {
    this.ws = new SockJS('http://localhost:2797/ws');
    this.stompClient = Stomp.over(this.ws);
    this.stompClient.connect({}, this.onConnected, this.onError);
  }

  onConnected = () => {
    console.log('connected');
    console.log(this.sessionUser);
    this.stompClient.subscribe(
      '/usr/' + this.sessionUser.id + '/queue/messages',
      this.onMessageReceived
    );
  }

  onError = (error) => {
    console.log(error);
  }

  onMessageReceived = (msg) => {
      const notification = JSON.parse(msg.body);
      console.log(notification);

      if (this.selectUser.id === notification.senderId) {
        this.chatService.findChatMessage(notification.id).subscribe(
          (response: ChatMessage) => {
            this.chatMessages.push(response);
          }
        ),
        (error: HttpErrorResponse) => {
        console.log(error);
        };
      }
  }

  sendMessage(msg: NgForm) {
    const message = msg.form.value.msg.trim();
    this.flag = false;
    if (message && this.stompClient) {
      const m = new ChatMessage();
      m.senderId = this.sessionUser.id;
      m.recipientId = this.selectUser.id;
      m.senderName = this.sessionUser.username;
      m.recipientName = this.selectUser.username;
      m.content = message;
      m.timestamp = new Date();
      this.stompClient.send('/app/chat', {}, JSON.stringify(m));
      // @ts-ignore
      document.getElementById('newMessage').value = '';
      this.chatMessages.push(m);
    }
  }

  channels() {
    this.userService.getAllUsersSystem().subscribe(
      (response: User[]) => {
        this.listUsers = response.filter(u => u.id !== this.sessionUser.id);
      },
      (error: HttpErrorResponse) => {
        console.log(error.message);
      }
    );
  }

  private findMessages() {
    this.chatService.findAllMesages(this.sessionUser.id, this.selectUser.id).subscribe(
      (response: ChatMessage[]) => {
        this.chatMessages = response;
        if (response.length === 0) {
          this.flag = true;
        } else {
          this.flag = false;
        }
      },
      (error: HttpErrorResponse) => {
        console.log(error.message);
      }
    );
  }

  selectChat(user: User) {
    this.selectUser = user;
    this.findMessages();
  }

  private authSession() {
    this.sessionUser = this.authService.getUserFromLocalCache();
  }

  searchUsers(searchUsers: string) {
    const res: User[] = [];
    for (const user of this.listUsers) {
      if (user.firstname.toLowerCase().indexOf(searchUsers.toLowerCase()) !== -1 ||
        user.lastname.toLowerCase().indexOf(searchUsers.toLowerCase()) !== -1 ||
        user.username.toLowerCase().indexOf(searchUsers.toLowerCase()) !== -1) {
        res.push(user);
      }
    }
    this.listUsers = res;
    if (res.length === 0 ||
      !searchUsers) {
      this.channels();
    }
  }
}
