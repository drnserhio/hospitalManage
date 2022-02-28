import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../service/auth-service.service';
import {Router} from '@angular/router';
import {NotifyService} from '../service/notify-service.service';
import {Subscription} from 'rxjs';
import {User} from '../model/user';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {HeaderType} from '../enum/header-type.enum';
import {NotifyType} from '../enum/notify-type.enum';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  loadingIcon: boolean;
  private subscription: Subscription[] = [];

  constructor(private router: Router,
              private authService: AuthService,
              private notifyService: NotifyService) { }

  ngOnInit(): void {
    if (this.authService.isUserLoggedIn()) {
      this.router.navigateByUrl('/user/home');
    } else {
      this.router.navigateByUrl('/login');
    }
  }

  ngOnDestroy(): void {
    this.subscription.forEach(sub => sub.unsubscribe());
  }

  public signIn(user: User): void {
    this.loadingIcon = true;
    this.subscription.push(
      this.authService.login(user).subscribe(
        (response: HttpResponse<User>) => {
          const token = response.headers.get(HeaderType.JWT_TOKEN);
          this.authService.saveToken(token);
          this.authService.saveUserToLocalCahe(response.body);
          this.router.navigateByUrl('/user/home');
          this.loadingIcon = false;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotifyError(NotifyType.ERROR, errorResponse.error.message);
          this.loadingIcon = false;

        }
      )
    );
  }

 private sendNotifyError(notifyType: NotifyType, message: string): void {
    if (message) {
      this.notifyService.sendNotify(notifyType, message);
    } else {
      this.notifyService.sendNotify(notifyType, 'An occured. Please try again.');
    }
 }
}
