import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {User} from '../model/user';
import {AnalyzeICDDate} from '../model/analyzeICDDate';
import {RequestTable} from '../dto/request-table';
import {ResponseTable} from '../dto/response-table';
import {Treatment} from "../model/treatment";

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  private host: string = environment.urlSpring;

  constructor(private httpClient: HttpClient) { }

  public addAnalyze(userId: number, icdName: string): Observable<void | HttpErrorResponse> {
    return this.httpClient.post<void>(`${this.host}/account/diagnosis/${userId}`, icdName);
  }

  public addTreatment(userId: number, treatment: string): Observable<void | HttpErrorResponse> {
    return this.httpClient.post<void>(`${this.host}/account/add-treatment/${userId}`, treatment);
  }

  public deleteChooseTreatment(userId: number, treatmentId: number): Observable<void | HttpErrorResponse> {
    return this.httpClient.delete<void>(`${this.host}/account/del/choose/treatment/${userId}/${treatmentId}`);
  }

  public changeHospitalization(username: string, hospitalization: string) {
    return this.httpClient.post<User>(`${this.host}/account/change/hospitalization/${username}`, hospitalization);
  }

  public getAllAnaliziesByUserIdPage(request: RequestTable, id: number): Observable<ResponseTable<AnalyzeICDDate>> {
    return this.httpClient.post<ResponseTable<AnalyzeICDDate>>(`${this.host}/account/list/analize/user/${id}`, request);
  }

  public deleteAnalizeFromUser(userId: number, analizeId: number): Observable<boolean> {
    return this.httpClient.delete<boolean>(`${this.host}/account/delete/analize/${userId}/${analizeId}`);
  }

  public logout(user: User): Observable<boolean> {
    return this.httpClient.put<boolean>(`${this.host}/account/logout`, user);
  }

  public updateTreatment(trearment: Treatment): Observable<boolean> {
    return this.httpClient.put<boolean>(`${this.host}/account/treatment-change`, trearment);
  }
}
