import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {ChatMessage} from '../model/chat/chat-message';
import {Observable} from 'rxjs';
import * as SockJS from 'sockjs-client';
import {Stomp} from '@stomp/stompjs';
import {AuthService} from './auth-service.service';
import {User} from '../model/user';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private host = environment.urlSpring;

  constructor(private http: HttpClient) {
  }

  public findAllMesages(senderId: number, recipientId: number): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${this.host}/messages/${senderId}/${recipientId}`);
  }

  public sendMessage(msg: ChatMessage): Observable<void> {
    return this.http.post<void>(`${this.host}/send`, msg);
  }

  public findChatMessage(id: number): Observable<ChatMessage> {
    return this.http.get(`${this.host}/messages/${id}`);
  }
}

