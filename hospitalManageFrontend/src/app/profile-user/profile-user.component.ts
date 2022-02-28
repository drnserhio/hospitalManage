import {Component, OnInit} from '@angular/core';
import {User} from '../model/user';
import {UserService} from '../service/user-service.service';
import {AuthService} from '../service/auth-service.service';
import {Router} from '@angular/router';
import {NotifierService} from 'angular-notifier';
import {NotifyType} from '../enum/notify-type.enum';
import {HttpErrorResponse} from '@angular/common/http';
import {RoleUser} from '../enum/role-user.enum';
import {ProfileService} from '../service/profile-service.service';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {CustomHttpResponse} from "../model/custom-http-response";
import {NgForm} from "@angular/forms";

const EXCEPTION_NEW_PASS_AND_VERIFY = 'NEW PASSWORD AND VERIFY DON\'T SAME. PLEASE ENTRY CORRECT NEW PASSWORD.';
const YOU_PASS_IS_NOT_VALID = 'YOUR PASSWORD IS NOT VALID';

@Component({
  selector: 'app-profile-user',
  templateUrl: './profile-user.component.html',
  styleUrls: ['./profile-user.component.css']
})
export class ProfileUserComponent implements OnInit {

  userProfile: User;
  private editProfile: User;
  private flag = true ;
  private filename: string;
  private profileImage: File;
  public loadIcon: boolean;
  settingsFlag = true;
  deleteFlag = true;
  questionFlag = false;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private profileService: ProfileService,
    private router: Router,
    private notify: NotifierService,
    private modalService: NgbModal) { }

  ngOnInit() {
    this.loadUser();
    this.userProfile = this.authService.getUserFromLocalCache();
    this.getUser();
  }

  public getUser(): void {
    if (this.editProfile == null) {
        this.editProfile = this.userProfile;
      }
    this.getRole();
  }

  public getRole(): void {
    if (this.userProfile.role !== RoleUser.ROLE_USER) {
      this.flag = false;
    }
  }

  public changeProfile(): void {
    this.loadIcon = true;

    const formData = this.userService.createFormDataForUpdateProfile(this.userProfile.username, this.editProfile);
    this.userService.updateProfile(formData).subscribe(
      (response: User) => {
        this.userProfile = response;
        this.authService.saveUserToLocalCahe(response);
        this.notify.notify(NotifyType.SUCCESS, 'Change you information successfully');
        this.loadIcon = false;
      },
      (error: HttpErrorResponse) => {
        this.notify.notify(NotifyType.ERROR, 'You entry bad information!');
        this.loadIcon = false;
      }
    );
  }

  public changeProfileImage(file: File): void {
    this.profileImage = file;
  }

  private saveNewProfileImage(): void {
    const formDate = this.userService.createFormDataForUpdateProfileImage(this.userProfile.username, this.profileImage);
    this.userService.updateProfileImage(formDate).subscribe(
      (response: User) => {
        this.userProfile = response;
        this.authService.saveUserToLocalCahe(response);
        this.notify.notify(NotifyType.SUCCESS, 'You update your image');
        this.profileImage = null;
        this.filename = null;
        this.loadUser();
        window.location.reload();
        this.clickButton('close-change-image');
      },
      (error: HttpErrorResponse) => {
        this.notify.notify(NotifyType.ERROR, 'You image don\'t update. Please try again.');
        this.profileImage = null;
        this.clickButton('close-change-image');
      }
    );
  }

  private clickButton(button: string) {
    document.getElementById(button).click();
  }

  public onLogOut(): void {
    console.log(this.userProfile);
    this.profileService.logout(this.userProfile).subscribe(
      (response: boolean) => {
        if (response) {
          this.authService.logOut();
          this.notify.notify(NotifyType.SUCCESS, 'You successfully log out.');
          this.router.navigate(['/login']);
        }
      }
    );
  }

  public onToUsersList(): void {
    this.router.navigate([`/user/home`]);
  }

  onSettings() {
    if (this.settingsFlag === true) {
      this.settingsFlag = false;
    } else {
      this.settingsFlag = true;
    }
  }

  onDeleteIcon() {
    if (this.deleteFlag) {
      this.deleteFlag = false;
    } else {
      this.deleteFlag = true;
    }
  }

  onDeleteQuestionModal() {
    this.deleteFlag = true;
    this.questionFlag = false;
  }

  public close() {
    this.questionFlag = false;
    this.modalService.dismissAll();
  }

  changeQuestionFlag() {
    this.questionFlag = true;
  }

  onDeleteCurrenstUser() {
    this.userService.deleteUser(this.userProfile.username).subscribe(
      (response: CustomHttpResponse) => {
        this.authService.logOut();
        this.notify.notify(NotifyType.SUCCESS, 'You deleted account successful.')
        this.clickButton('closeModal');
        this.router.navigateByUrl("/login")
      },
      (error: HttpErrorResponse) => {
        this.notify.notify(NotifyType.WARNING, 'Check your input username in field!')
      }
    )
  }

  validUsername(username: string) {
    if (this.userProfile.username.endsWith(username)) {
      this.onDeleteCurrenstUser();
    } else {
      this.notify.notify(NotifyType.WARNING, 'Check your input username!');
    }
  }

  private loadUser() {
    const local = this.authService.getUserFromLocalCache();
    console.log(local);
    this.userService.findUser(local.username).subscribe(
      (response: User) => {
        this.authService.setUserToLocalCache(response);
      },
      (error: HttpErrorResponse) => {
        console.log(error.message);
      }
    )
  }

  onChangePassword(ngForm: NgForm) {
    const formData = this.userService.createFormDateForChangePassword(
      ngForm.value.oldPassword,
      ngForm.value.newPassword,
      ngForm.value.verifyPassword,
    );
    this.userService.changePassword(this.userProfile.username, formData).subscribe(
      (response: Boolean) => {
          this.notify.notify(NotifyType.SUCCESS, 'You changed successful password.');
          // @ts-ignore
        document.getElementById('oldPassword').value = '';
          // @ts-ignore
        document.getElementById('newPassword').value = '';
          // @ts-ignore
        document.getElementById('verifyPassword').value = '';
      },
      (error: HttpErrorResponse) => {
        this.notify.notify(NotifyType.WARNING, error.error.message);
        console.log(error.error.message);
        if (error.error.message.endsWith(EXCEPTION_NEW_PASS_AND_VERIFY)) {
          // @ts-ignore
          document.getElementById('newPassword').value = '';
          // @ts-ignore
          document.getElementById('verifyPassword').value = '';
        }
        if (error.error.message.endsWith(YOU_PASS_IS_NOT_VALID)) {
          // @ts-ignore
          document.getElementById('oldPassword').value = '';
        }
      }
    )
  }
}

