import {Component, OnInit} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ResponseTable} from '../dto/response-table';
import {Treatment} from '../model/treatment';
import {RequestTable} from '../dto/request-table';
import {UserService} from '../service/user-service.service';
import {NotifyService} from '../service/notify-service.service';
import {Router} from '@angular/router';
import {User} from '../model/user';
import {ProfileSaveHelper} from '../profile-select/profile-save-helper';
import {AuthService} from '../service/auth-service.service';
import {NotifyType} from '../enum/notify-type.enum';
import {HttpErrorResponse} from '@angular/common/http';
import {ProfileService} from '../service/profile-service.service';

@Component({
  selector: 'app-treatment-card',
  templateUrl: './treatment-card.component.html',
  styleUrls: ['./treatment-card.component.css']
})
export class TreatmentCardComponent implements OnInit {

  public flag = false;
  public treatmens: Treatment[] = [];
  private column = 'date_create';
  private sort = 'asc';
  private page = 1;
  private size = 5;
  pageSizes = [5, 10, 25, 50, 100]
  private countSort = 3;

  private activeColumn: string;

  public requestTable = new RequestTable(this.column, this.sort, this.page, this.size);
  public responseTable: ResponseTable<Treatment> = new ResponseTable<Treatment>();
  private selectUser: User;
  private sessionUser: User;
  loadIcon: boolean;
  private selectUpdateTreatment: Treatment;

  constructor(private userService: UserService,
              private notify: NotifyService,
              private router: Router,
              private saveServiceSelectedUser: ProfileSaveHelper,
              private authService: AuthService,
              private profileService: ProfileService,
              private modalService: NgbModal) { }

  ngOnInit() {
    this.sessionUser = this.authService.getUserFromLocalCache();
    this.selectUser = this.saveServiceSelectedUser.getFromStorageUser();
    this.getTreatmentsByUserId();
  }

  public searchUsers(searchUsers: string): void {
    const res: Treatment[] = [];
    for (const treatment of this.treatmens) {
      if (treatment.treatment.toLowerCase().indexOf(searchUsers.toLowerCase()) !== -1 ||
        treatment.dateCreate.toString().toLowerCase().indexOf(searchUsers.toLowerCase()) !== -1 ) {
        res.push(treatment);
      }
    }
    this.treatmens = res;
    if (res.length === 0 ||
      !searchUsers) {
      this.getTreatmentsByUserId();
    }
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
    this.countSort = 3;
    this.activeColumn = '';
    // @ts-ignore
    document.getElementById('changeValue').value ='5';
    this.getTreatmentsByUserId();
  }

  public getResetTreatmentPageWithField() {
    this.requestTable.sort = 'desc';
    this.requestTable.column = 'date_create';
    this.countSort = 3;
    this.activeColumn = '';
    this.getTreatmentsByUserId();
  }

  private getTreatmentsByUserId() {
    const id = this.selectUser.id;
    this.userService.getTreatmentsByUserId(this.requestTable, id).subscribe(
      (response: ResponseTable<Treatment>) => {
        this.treatmens =  response.content;
        this.responseTable = response;
        if (this.treatmens.length === 0) {
          this.notify.sendNotify(NotifyType.WARNING, 'This patient don\'t have treatments.');
          this.router.navigate(['/selectprofile']);
        } else {
          this.notify.sendNotify(NotifyType.SUCCESS, `${this.treatmens.length} treatment(s) loaded successfully`);
        }
      },
      (error) => {
        console.log(error.message);
      }
    );
  }

  public deleteChooseTreatment(id: number): void {
    this.profileService.deleteChooseTreatment(this.selectUser.id, id).subscribe(
      (value) => {
        this.updateUser();
        this.notify.sendNotify(NotifyType.SUCCESS, 'You deleted choose text seccessfull.');
        this.getTreatmentsByUserId();
      },
      (error: HttpErrorResponse) => {
        this.notify.sendNotify(NotifyType.ERROR, 'You dont\' delete choose text. Error operation.Please try again.');
      }
    );
  }

  public openModal(content) {
    this.modalService.open(content, {size: 'md'});
  }

  public close() {
    this.modalService.dismissAll();
  }

  onSelectTreatmentDiagnos(treatment: Treatment) {
    this.selectUpdateTreatment = treatment;
    this.clickButton('openModalUpdateTreatment');
  }

  public clickButton(button: string) {
    document.getElementById(button).click();
  }

  updateTreatmentDiagnos() {
    console.log(this.selectUpdateTreatment);
    this.profileService.updateTreatment(this.selectUpdateTreatment).subscribe(
      (response: boolean) => {
        this.notify.sendNotify(NotifyType.SUCCESS, 'Treatment update successful');
        this.close();
      },
      (error: HttpErrorResponse) => {
        this.notify.sendNotify(NotifyType.WARNING, 'You entry badly change in treatment.Check and try again.');
        this.close();
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
      this.sort = 'desc';
      this.page = 1;
      this.requestTable.sort = this.sort;
      this.requestTable.column = this.column;
      this.requestTable.page = this.page;
      this.getTreatmentsByUserIdWIthNewField();
    } else {
      this.sort = 'asc';
      this.page = 1;
      this.requestTable.column = this.column;
      this.requestTable.sort = this.sort;
      this.requestTable.page = this.page;
      this.getTreatmentsByUserIdWIthNewField();
    }
  }

  private getTreatmentsByUserIdWIthNewField() {
    const id = this.selectUser.id;
    this.userService.getTreatmentsByUserId(this.requestTable, id).subscribe(
      (response: ResponseTable<Treatment>) => {
        this.treatmens =  response.content;
        this.responseTable = response;
        if (this.treatmens.length === 0) {
          this.notify.sendNotify(NotifyType.WARNING, 'This patient don\'t have treatments.');
          this.router.navigate(['/selectprofile']);
        } else {
          this.notify.sendNotify(NotifyType.SUCCESS, `${this.treatmens.length} treatment(s) loaded successfully`);
        }
      },
      (error ) => {
        console.log(error.message);
      }
    );
  }

  private updateUser() {
    const usr = this.userService.findUser(this.selectUser.username).subscribe(
      (response: User) => {
        this.selectUser = response;
        this.saveServiceSelectedUser.addSelecteUserToStorage(response);
        this.notify.sendNotify(NotifyType.INFO, 'User update successful.')
      },
      (error: HttpErrorResponse) => {
        this.notify.sendNotify(NotifyType.SUCCESS, 'User didn\'t update.')
      }
    )

  }
}
