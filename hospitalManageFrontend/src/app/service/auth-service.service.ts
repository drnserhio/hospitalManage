import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment';
import { JwtHelperService} from '@auth0/angular-jwt';
import {User} from '../model/user';
import {Observable} from 'rxjs';
import {HttpClient, HttpResponse} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  host: string = environment.urlSpring;
  private token: string;
  private usernameLogeedIn: string;
  private jwtHelper: JwtHelperService = new JwtHelperService();
  constructor(private httpCLient: HttpClient) { }

  public login(user: User): Observable<HttpResponse<User>> {
    return this.httpCLient.post<User>(`${this.host}/user/login`, user, { observe: 'response'});
  }

  public register(user: User): Observable<User> {
    return this.httpCLient.post<User>(`${this.host}/user/register`, user);
  }

  public logOut(): void {
    this.token = null;
    this.usernameLogeedIn = null;
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.removeItem('users');
  }

  public saveToken(token: string): void {
    this.token = token;
    localStorage.setItem('token', token);
  }

  public saveUserToLocalCahe(user: User): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  public setUserToLocalCache(user: User): void {
    localStorage.removeItem('user')
    localStorage.setItem('user', JSON.stringify(user));
}

  public getUserFromLocalCache(): User {
    return JSON.parse(localStorage.getItem('user'));
  }

  public loadToken(): void {
    this.token = localStorage.getItem('token');
  }

  public getToken(): string {
    return this.token;
  }

  public isUserLoggedIn(): boolean {
    this.loadToken();
    if (this.token != null && this.token !== '') {
      if (this.jwtHelper.decodeToken(this.token).sub != null || '') {
        if (this.jwtHelper.getTokenExpirationDate(this.token)) {
          this.usernameLogeedIn = this.jwtHelper.decodeToken(this.token).sub;
          return true;
        }
      }
    } else {
      this.logOut();
      return false;
    }
  }
}
