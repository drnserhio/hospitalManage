import {Component, OnInit} from '@angular/core';
import {Treatment} from '../model/treatment';
import {RequestTable} from '../dto/request-table';
import {ResponseTable} from '../dto/response-table';
import {User} from '../model/user';
import {UserService} from '../service/user-service.service';
import {NotifyService} from '../service/notify-service.service';
import {Router} from '@angular/router';
import {ProfileSaveHelper} from '../profile-select/profile-save-helper';
import {AuthService} from '../service/auth-service.service';
import {ProfileService} from '../service/profile-service.service';
import {NotifyType} from '../enum/notify-type.enum';
import {HttpErrorResponse} from '@angular/common/http';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-treatments-card-user-profile',
  templateUrl: './treatments-card-user-profile.component.html',
  styleUrls: ['./treatments-card-user-profile.component.css']
})
export class TreatmentsCardUserProfileComponent implements OnInit {

  public treatmens: Treatment[] = [];
  private column = 'date_create';
  sort = 'asc';
  private page = 1;
  private size = 5;
  pageSizes = [5, 10, 25, 50, 100];

  public requestTable = new RequestTable(this.column, this.sort, this.page, this.size);
  public responseTable: ResponseTable<Treatment> = new ResponseTable<Treatment>();
  private sessionUser: User;
  loadIcon: boolean;
  private selectTreatment: Treatment;
  flagSort = true;

  private countSort = 3;
  private activeColumn: string;

  constructor(private userService: UserService,
              private notify: NotifyService,
              private router: Router,
              private saveServiceSelectedUser: ProfileSaveHelper,
              private authService: AuthService,
              private modalService: NgbModal) { }

  ngOnInit() {
    this.sessionUser = this.authService.getUserFromLocalCache();
    this.loadUser();
    this.getTreatmentsByUserId();
  }

  public searchUsers(searchUsers: string): void {
    const res: Treatment[] = [];
    for (const treatment of this.treatmens) {

      if (treatment.treatment.toLowerCase().indexOf(searchUsers.toLowerCase()) !== -1 ||
        treatment.dateCreate.toString().toLowerCase().indexOf(searchUsers.toLowerCase()) !== -1) {
        res.push(treatment);
      }
    }
    this.treatmens = res;
    if (res.length === 0 ||
      !searchUsers) {
      this.getTreatmentsByUserId();
    }
  }

  public openModal(content) {
    this.modalService.open(content, {size: 'md'});
  }

  public close() {
    this.modalService.dismissAll();
  }

  pageChangeShowEntity(event) {
    this.size = event.target.value;
    this.page = 1;
    this.requestTable.size = this.size;
    this.requestTable.page = this.page;
    this.requestTable.sort = 'desc';
    this.countSort = 3;
    this.getTreatmentsByUserId();
  }

  pageChange(event) {
    this.page = event;
    this.requestTable.page = this.page;
    this.getTreatmentsByUserIdWIthNewField();
  }

  public getResetTreatmentPage() {
    this.requestTable.size = 5;
    this.requestTable.sort = 'desc';
    this.requestTable.column = 'date_create';
    this.activeColumn = '';
    this.countSort = 3;
    // @ts-ignore
    document.getElementById('changeValue').value = '5';
    this.getTreatmentsByUserId();
  }

  public getResetTreatmentPageWithField() {
    this.requestTable.sort = 'desc';
    this.requestTable.column = 'date_create';
    this.activeColumn = '';
    this.countSort = 3;
    this.getTreatmentsByUserId();
  }

  private getTreatmentsByUserId() {
    const id = this.sessionUser.id;
    this.userService.getTreatmentsByUserId(this.requestTable, id).subscribe(
      (response: ResponseTable<Treatment>) => {
        this.treatmens = response.content;
        this.responseTable = response;
        if (this.treatmens.length === 0) {
          this.notify.sendNotify(NotifyType.WARNING, 'You don\'t have treatments.');
          this.router.navigate(['/profile']);
        } else {
          this.notify.sendNotify(NotifyType.SUCCESS, `${this.treatmens.length} treatment(s) loaded successfully`);
        }
      },
      (error: HttpErrorResponse) => {
        console.log(error.message);
      }
    );
  }

  public sortByColumn($event) {
    this.countSort--;
    if (this.countSort === 0) {
      this.countSort = 3;
      this.sort = 'asc';
      this.activeColumn = '';
      this.getResetTreatmentPageWithField()
      return;
    }
    // @ts-ignore
    this.column = event.srcElement.id;
    this.activeColumn = this.column;
    if (this.sort.endsWith('asc')) {
      this.flagSort = false;
      this.sort = 'desc';
      this.page = 1;
      this.requestTable.sort = this.sort;
      this.requestTable.column = this.column;
      this.requestTable.page = this.page;
      this.getTreatmentsByUserIdWIthNewField();
    } else {
      this.flagSort = false;
      this.sort = 'asc';
      this.page = 1;
      this.requestTable.column = this.column;
      this.requestTable.sort = this.sort;
      this.requestTable.page = this.page;
      this.getTreatmentsByUserIdWIthNewField();
    }
  }

  private getTreatmentsByUserIdWIthNewField() {
    const id = this.sessionUser.id;
    this.userService.getTreatmentsByUserId(this.requestTable, id).subscribe(
      (response: ResponseTable<Treatment>) => {
        this.treatmens = response.content;
        this.responseTable = response;
        if (this.treatmens.length === 0) {
          this.notify.sendNotify(NotifyType.WARNING, 'This patient don\'t have treatments.');
          this.router.navigate(['/selectprofile']);
        } else {
          this.notify.sendNotify(NotifyType.SUCCESS, `${this.treatmens.length} treatment(s) loaded successfully`);
        }
      },
      (error: HttpErrorResponse) => {
        console.log(error.message);
      }
    );
  }

  private clickButton(btn: string) {
    document.getElementById(btn).click();
  }

  onSelectTreatmentDiagnos(treatment: Treatment) {
    this.selectTreatment = treatment;
    console.log(treatment);
    this.clickButton('openModalTranslationTreatment');
  }

  private loadUser() {
    const local = this.authService.getUserFromLocalCache();
    console.log(local);
    this.userService.findUser(local.username).subscribe(
      (response: User) => {
        console.log(response);
        this.authService.setUserToLocalCache(response);
      },
      (error: HttpErrorResponse) => {
        console.log(error.message);
      }
    )
  }
}
