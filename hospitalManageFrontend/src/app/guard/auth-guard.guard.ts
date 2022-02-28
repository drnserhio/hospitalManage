import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {AuthService} from '../service/auth-service.service';
import {NotifyService} from '../service/notify-service.service';
import {NotifyType} from '../enum/notify-type.enum';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService,
              private router: Router,
              private notifyService: NotifyService) {
  }
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    return this.isUserLoggedIn();
  }

  private isUserLoggedIn(): boolean {
    if (this.authService.isUserLoggedIn()) {
      return true;
    } else {
      this.router.navigate([`/login`]);
      this.notifyService.sendNotify(NotifyType.ERROR, 'You need to login or registration to access this page');
      return false;
    }
  }
}
