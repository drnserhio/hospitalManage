import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ICD} from '../model/ICD';

@Injectable({
  providedIn: 'root'
})
export class ICDservice {

  private host: string = environment.urlSpring;

  constructor(private httpClient: HttpClient) { }

  public getICD(code: string): Observable<ICD | HttpErrorResponse> {
    return this.httpClient.get<ICD>(`${this.host}/icd/${code}`);
  }
}
