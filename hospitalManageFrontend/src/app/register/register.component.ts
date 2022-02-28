import {Component, OnDestroy, OnInit} from '@angular/core';
import {User} from '../model/user';
import {HttpErrorResponse} from '@angular/common/http';
import {NotifyType} from '../enum/notify-type.enum';
import {Router} from '@angular/router';
import {AuthService} from '../service/auth-service.service';
import {NotifyService} from '../service/notify-service.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, OnDestroy {
  loadingIcon: boolean;
  private subscription: Subscription[] = [];

  constructor(private router: Router,
              private authService: AuthService,
              private notifyService: NotifyService) { }

  ngOnInit(): void {
    if (this.authService.isUserLoggedIn()) {
      this.router.navigateByUrl('/user/home');
    }
  }

  ngOnDestroy(): void {
    this.subscription.forEach(sub => sub.unsubscribe());
  }

  public registration(user: User): void {
    this.loadingIcon = true;
    this.subscription.push(
      this.authService.register(user).subscribe(
        (response: User) => {
          this.loadingIcon = false;
          this.sendNotify(NotifyType.SUCCESS, `A new account was created for ${response.username}.
          Sign in to website`);
          this.router.navigateByUrl("/login");
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotify(NotifyType.ERROR, errorResponse.error.message);
          this.loadingIcon = false;
        }
      )
    );
  }

  private sendNotify(notifyType: NotifyType, message: string): void {
    if (message) {
      this.notifyService.sendNotify(notifyType, message);
    } else {
      this.notifyService.sendNotify(notifyType, 'An occured. Please try again.');
    }
  }
}
