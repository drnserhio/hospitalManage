import { Injectable } from '@angular/core';
import {User} from '../model/user';

@Injectable({
  providedIn: 'root'
})
export class ProfileSaveHelper {

  constructor() { }

  public addSelecteUserToStorage(selectedUser: User): void {
    localStorage.setItem('saveUser', JSON.stringify(selectedUser));
  }

  public getFromStorageUser(): User {
    return JSON.parse(localStorage.getItem('saveUser'));
  }

  public setUserFromStorageUser(selectUser: User) {
    localStorage.removeItem('saveUser');
    localStorage.setItem('saveUser', JSON.stringify(selectUser));
  }
}
