import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import { Observable} from 'rxjs';
import { AuthService} from '../service/auth-service.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) { }

  intercept(httpRequest: HttpRequest<any>, httpHandler: HttpHandler): Observable<HttpEvent<any>> {
    if (httpRequest.url.includes(`${this.authService.host}/user/login`)) {
      return httpHandler.handle(httpRequest);
    }
    if (httpRequest.url.includes(`${this.authService.host}/user/register`)) {
      return httpHandler.handle(httpRequest);
    }
    if (httpRequest.url.includes(`${this.authService.host}/user/changepass`)) {
      return httpHandler.handle(httpRequest);
    }

    this.authService.loadToken();
    const token = this.authService.getToken();
    const request = httpRequest.clone({setHeaders: { Authorization: `Bearer ${token}`}});
    return httpHandler.handle(request);
  }
}
