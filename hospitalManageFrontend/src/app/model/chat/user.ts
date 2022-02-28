import {ChatRoom} from "./chat-room";

export class User {

  id?: number;
  username?: string
  chatRooms?: ChatRoom[] = [];
}
