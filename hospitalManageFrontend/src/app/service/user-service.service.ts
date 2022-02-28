import { Injectable } from '@angular/core';
import {User} from '../model/user';
import {AuthService} from './auth-service.service';
import {Router} from '@angular/router';
import {HttpClient, HttpErrorResponse, HttpEvent, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {CustomHttpResponse} from '../model/custom-http-response';
import {ResponseTable} from '../dto/response-table';
import {Treatment} from "../model/treatment";
import {RequestTable} from "../dto/request-table";
import {Video} from "../model/video";
@Injectable({
  providedIn: 'root'
})
export class UserService {

  private host: string = environment.urlSpring;

  constructor(private httpClient: HttpClient) { }

  public getUserPage(request: RequestTable): Observable<ResponseTable<User>> {
    return this.httpClient.post<ResponseTable<User>>(`${this.host}/user/list-page`, request);
  }


  public getVideosPageByUserId(request: RequestTable, userId: number): Observable<ResponseTable<Video>> {
  return this.httpClient.post<ResponseTable<Video>>(`${this.host}/user/videos/in/user/${userId}`, request);
}

  public getAllUsersSystem(): Observable<User[] | HttpErrorResponse> {
    return this.httpClient.get<User[]>(`${this.host}/user/systemusers`);
  }

  public addUser(formData: FormData): Observable<User | HttpErrorResponse> {
    return this.httpClient.post<User>(`${this.host}/user/add`, formData);
  }

  public updateUser(formData: FormData): Observable<User | HttpErrorResponse> {
    return this.httpClient.put<User>(`${this.host}/user/update`, formData);
  }

  public updateProfile(formData: FormData): Observable<User | HttpErrorResponse> {
    return this.httpClient.put<User>(`${this.host}/account/updateProfile`, formData);
  }

  public changePassword(username: string, formData: FormData): Observable<Boolean> {
    return this.httpClient.put<Boolean>(`${this.host}/user/change_pass/${username}`, formData);
  }

  public updateProfileImage(formData: FormData): Observable<User | HttpErrorResponse> {
    return this.httpClient.post<User>(`${this.host}/user/updateProfileImage`, formData);
  }

  public deleteUser(username: string): Observable<CustomHttpResponse | HttpErrorResponse> {
    return this.httpClient.delete<CustomHttpResponse>(`${this.host}/user/delete/${username}`);
  }

  public getDocument(username: string): Observable<Blob> {
    return this.httpClient.get(`${this.host}/account/document/${username}`, { responseType: 'blob' });
  }

  public findUser(username: string): Observable<User | HttpErrorResponse> {
    return this.httpClient.get<User>(`${this.host}/user/find/${username}`);
}

  public getTreatmentsByUserId(request: RequestTable, userId: number): Observable<ResponseTable<Treatment>> {
    return this.httpClient.post <ResponseTable<Treatment>>(`${this.host}/user/treatments/in/user/${userId}`, request);
  }

  public createFormDataForUpdateProfile(usernameCurrent: string, user: User): FormData {
    const formData = new FormData();
    formData.append('currentUsername', usernameCurrent);
    formData.append('firstname', user.firstname);
    formData.append('lastname', user.lastname);
    formData.append('patronomic', user.patronomic);
    formData.append('age', JSON.parse(String(user.age)));
    formData.append('username', user.username);
    formData.append('email', user.email);
    formData.append('QRCODE', user.qrcode);
    formData.append('address', user.address);
    formData.append('infoAboutComplaint', user.infoAboutComplaint);
    formData.append('infoAboutSick', user.infoAboutSick);
    return formData;
  }

  public createFormDataForUpdateAccessProfile(json: { role: any; isNotLocked: any; username: string }): FormData {
    const formData = new FormData();
    formData.append('username', json.username);
    formData.append('role', json.role);
    formData.append('isNonLocked', json.isNotLocked);
    return formData;
  }

  public createFormDateForChangePassword(oldPassword: string, newPassword: string, verifyPassword: string): FormData {
    const formDate = new FormData();
    formDate.append('oldPassword', oldPassword);
    formDate.append('newPassword', newPassword);
    formDate.append('verifyPassword', verifyPassword);
    return formDate;
  }

  public createFormDataForUpdateProfileImage(username: string, profileImage: File): FormData {
    const formData = new FormData();
    formData.set('username', username);
    formData.set('profileImage', profileImage);
    return formData;
  }

}
