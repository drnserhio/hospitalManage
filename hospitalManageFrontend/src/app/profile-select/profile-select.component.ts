import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {NotifyService} from '../service/notify-service.service';
import {User} from '../model/user';
import {ProfileSaveHelper} from './profile-save-helper';
import {NotifyType} from '../enum/notify-type.enum';
import {HttpErrorResponse} from '@angular/common/http';
import {UserService} from '../service/user-service.service';
import {AuthService} from '../service/auth-service.service';
import {RoleUser} from '../enum/role-user.enum';
import {ICD} from '../model/ICD';
import {ICDservice} from '../service/icd-service.service';
import {ProfileService} from '../service/profile-service.service';
import {Treatment} from '../model/treatment';
import {ResponseTable} from '../dto/response-table';
import {RequestTable} from '../dto/request-table';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {CustomHttpResponse} from "../model/custom-http-response";
import {NgForm} from "@angular/forms";

@Component({
  selector: 'app-profile-select',
  templateUrl: './profile-select.component.html',
  styleUrls: ['./profile-select.component.css']
})
export class ProfileSelectComponent implements OnInit {
  private profileImage: File;
  private filename: string;
  private oldUsername: string;

  private selectICD: ICD;
  private listICD = new Array();
  updateSelect: User = null;

  constructor(
    private router: Router,
    private notify: NotifyService,
    private saveServiceSelectedUser: ProfileSaveHelper,
    private userService: UserService,
    private authService: AuthService,
    private icdService: ICDservice,
    private profileService: ProfileService,
    private modalService: NgbModal) {
  }

  select: User;
  private loadIcon: boolean;
  private user: User;
  private flag = true;
  managmentFlag = true;

  private column = 'id';
  private sort = 'asc';
  private page = 1;
  private size = 3;
  pageSizes = [3, 6, 9];

  public requestTable = new RequestTable(this.column, this.sort, this.page, this.size);
  public responseTable: ResponseTable<Treatment> = new ResponseTable<Treatment>();
  settingsFlag = true;

  ngOnInit() {
    this.select = this.saveServiceSelectedUser.getFromStorageUser();
    this.updateSelect = this.saveServiceSelectedUser.getFromStorageUser();
    this.user = this.authService.getUserFromLocalCache();
    this.oldUsername = this.select.username;
    this.checkRole();
    this.loadUser();
  }

  private changeProfile(): void {
    this.loadIcon = true;
    const formData = this.userService.createFormDataForUpdateProfile(this.oldUsername, this.select);
    this.userService.updateProfile(formData).subscribe(
      (response: User) => {
        this.saveServiceSelectedUser.addSelecteUserToStorage(response);
        this.select = response;
        this.oldUsername = this.select.username;
        this.loadIcon = false;
        this.notify.sendNotify(NotifyType.SUCCESS, 'Change you information successfully');
        window.location.reload();
      },
      (error: HttpErrorResponse) => {
        this.notify.sendNotify(NotifyType.ERROR, 'You entry bad information!');
        this.loadIcon = false;
      }
    );
  }

  private checkRole(): void {
    if (this.user.role === RoleUser.ROLE_SUPER_ADMIN ||
      this.user.role === RoleUser.ROLE_ADMIN ||
      this.user.role === RoleUser.ROLE_DOCTOR) {
      this.flag = false;
    }
    if (this.user.role === RoleUser.ROLE_SECRETARY) {
      this.managmentFlag = false;
    }
  }

  private changeProfileImage(file: File): void {
    this.profileImage = file;
  }

  private saveNewProfileImage(): void {
    const formDate = this.userService.createFormDataForUpdateProfileImage(this.select.username, this.profileImage);

    this.userService.updateProfileImage(formDate).subscribe(
      (response: User) => {
        this.select = response;
        this.notify.sendNotify(NotifyType.SUCCESS, 'You update your image');
        this.profileImage = null;
        this.filename = null;
        this.clickButton('close-change-image');
        window.location.reload();
      },
      (error: HttpErrorResponse) => {
        this.notify.sendNotify(NotifyType.ERROR, 'You image don\'t update. Please try again.');
        this.profileImage = null;
        this.clickButton('close-change-image');
      }
    );
  }

  private clickButton(buttonId: string): void {
    document.getElementById(buttonId).click();
  }

  public searchICD(code: string): void {
    this.icdService.getICD(code).subscribe(
      (response: ICD) => {
        this.selectICD = response;
      },
      (error: HttpErrorResponse) => {
        this.selectICD = null;
      }
    );
  }

  public addToICDList(icd: ICD): void {
    if (icd !== null) {
      this.listICD.push(icd);
      this.selectICD = null;
      this.notify.sendNotify(NotifyType.SUCCESS, 'Your icd add successfull to Patient.');
      // @ts-ignore
      document.getElementById('searchICD').value = '';
    }
  }

  public deleteICDFromList(icd: ICD): void {
    this.listICD.pop().delete(icd);
    this.authService.saveUserToLocalCahe(this.select);
  }

  public sendListICD(): void {
    for (let icd of this.listICD) {
      this.profileService.addAnalyze(this.select.id, icd.value).subscribe(
        (value) => {
          this.notify.sendNotify(NotifyType.SUCCESS, 'List diagnosis save successfull');
          this.clickButton('close-edit-modal');
          this.updateUser();

        },
        (error: HttpErrorResponse) => {
          this.notify.sendNotify(NotifyType.ERROR, 'Check your list, you have wrong. Save Error. Please try again');
        }
      );
    }
  }

  public addTreatment(ngForm: NgForm): void {
    const treatment = ngForm.form.value.treatment;
    this.profileService.addTreatment(this.select.id, treatment).subscribe(
      (value) => {
        this.updateUser();
        // @ts-ignore
        document.getElementById('trt').value = '';
        this.notify.sendNotify(NotifyType.SUCCESS, 'You add successfull, info about treatment for Patient. Check Treatments card.');
      },
      (error: HttpErrorResponse) => {
        this.notify.sendNotify(NotifyType.ERROR, 'You treatment info don\'t save. Please check your text.');
      }
    );
  }

  public changeHospitalization(hospitalization: string): void {
    console.log(hospitalization);
    this.profileService.changeHospitalization(this.select.username, hospitalization).subscribe(
      (reasponse: User) => {
        this.select = reasponse;
        this.saveServiceSelectedUser.addSelecteUserToStorage(reasponse);
        this.clickButton('close-edit-modal-hospitalization');
        this.notify.sendNotify(NotifyType.SUCCESS, `Patient ${this.select.firstname} ${this.select.lastname} hospitalized.`);
      },
      (error: HttpErrorResponse) => {
        this.notify.sendNotify(NotifyType.ERROR, `Patient ${this.select.firstname} ${this.select.lastname} didn't hospitalization.`);
      }
    );
  }

  public OnToUsersList(): void {
    this.router.navigate([`/user/home`]);
  }

  onSettings() {
    if (this.settingsFlag === true) {
      this.settingsFlag = false;
    } else {
      this.settingsFlag = true;
    }
  }

  validUsername(username: string) {
    if (this.select.username.endsWith(username)) {
      this.onDeleteCurrenstUser();
    } else {
      this.notify.sendNotify(NotifyType.WARNING, 'Check your input username!');
    }
  }

  onDeleteCurrenstUser() {
    this.userService.deleteUser(this.select.username).subscribe(
      (response: CustomHttpResponse) => {
        this.notify.sendNotify(NotifyType.SUCCESS, 'You deleted account successful.')
        this.clickButton('close');
        this.router.navigateByUrl('/user/home');
      },
      (error: HttpErrorResponse) => {
        this.notify.sendNotify(NotifyType.WARNING, 'Check your input username in field!')
      }
    )
  }

  public close() {
    this.modalService.dismissAll();
  }

  updateAccessSelectProfile(ngForm: NgForm) {
    const json = {
      username: this.select.username,
      role: ngForm.value.role,
      isNotLocked: ngForm.value.isNotLocked
    }
   let formData = this.userService.createFormDataForUpdateAccessProfile(json);
    this.userService.updateUser(formData).subscribe(
      (response: User) => {
        this.select = response;
        this.saveServiceSelectedUser.setUserFromStorageUser(response);
        this.updateUser();
        this.close();
        this.notify.sendNotify(NotifyType.SUCCESS, 'You updated choose account, successful');
      },
      (error: HttpErrorResponse) => {
        this.notify.sendNotify(NotifyType.ERROR, 'You dont\' update account, check your put information!')
      }
    )
  }

  private updateUser() {
    this.userService.findUser(this.select.username).subscribe(
      (response: User) => {
        this.saveServiceSelectedUser.setUserFromStorageUser(response);
        this.select = this.saveServiceSelectedUser.getFromStorageUser();
      },
      (error: HttpErrorResponse) => {
        console.log(error.message);
      }
    )
  }

  private loadUser() {
    const local = this.authService.getUserFromLocalCache();
    this.userService.findUser(local.username).subscribe(
      (response: User) => {
        this.authService.setUserToLocalCache(response);
      },
      (error: HttpErrorResponse) => {
        console.log(error.message);
      }
    )
  }
}
