import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpEvent} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {User} from '../model/user';

@Injectable({providedIn: 'root'})
export class VideoService {

  private host: string = environment.urlSpring;

  constructor(private httpClient: HttpClient) { }

  public upload(username: string, formData: FormData): Observable<HttpEvent<string[]>> {
    return this.httpClient.post<string[]>(`${this.host}/video-operation/upload/${username}`, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }

  public download(username: string, fileName: string): Observable<HttpEvent<Blob>> {
    return this.httpClient.get(`${this.host}/video-operation/download/${username}/${fileName}`, {
      reportProgress: true,
      observe: 'events',
      responseType: 'blob'
    });
  }

  public removeFileInSetUser(username: string, filename: string): Observable<User | HttpErrorResponse> {
    return this.httpClient.delete<User>(`${this.host}/video-operation/remove/${username}/${filename}`);
  }
}
