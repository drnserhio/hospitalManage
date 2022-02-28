import {Component, OnInit} from '@angular/core';
import {RequestTable} from '../dto/request-table';
import {ResponseTable} from '../dto/response-table';
import {User} from '../model/user';
import {NotifyService} from '../service/notify-service.service';
import {Router} from '@angular/router';
import {ProfileSaveHelper} from '../profile-select/profile-save-helper';
import {ProfileService} from '../service/profile-service.service';
import {NotifyType} from '../enum/notify-type.enum';
import {RoleUser} from '../enum/role-user.enum';
import {AuthService} from '../service/auth-service.service';
import {HttpErrorResponse} from '@angular/common/http';
import {AnalyzeICDDate} from "../model/analyzeICDDate";
import {UserService} from "../service/user-service.service";

@Component({
  selector: 'app-analize-card-user-profile',
  templateUrl: './analize-card-user-profile.component.html',
  styleUrls: ['./analize-card-user-profile.component.css']
})
export class AnalizeCardUserProfileComponent implements OnInit {

  public flag = true;

  public analizes: AnalyzeICDDate[] = [];
  private column = 'date_add_analyze';
  private sort = 'asc';
  private page = 1;
  private size = 5;
  pageSizes = [5, 10, 15, 20, 25, 30, 35];

  public requestTable = new RequestTable(this.column, this.sort, this.page, this.size);
  public responseTable: ResponseTable<AnalyzeICDDate> = new ResponseTable<AnalyzeICDDate>();
  private selectUser: User;
  loadIcon: boolean;

  private activeColumn: string;
  private countSort = 3;


  constructor(private notify: NotifyService,
              private router: Router,
              private saveServiceSelectedUser: ProfileSaveHelper,
              private profileService: ProfileService,
              private authService: AuthService,
              private userService: UserService) { }

  ngOnInit() {
    this.selectUser = this.authService.getUserFromLocalCache();
    this.loadUser();
    this.getRoleSession();
    this.getAnaliziesByUserId();
  }

  public searchUsers(searchUsers: string): void {
    const res: AnalyzeICDDate[] = [];
    for (const az of this.analizes) {
      if (az.icdId.toLowerCase().indexOf(searchUsers.toLowerCase()) !== -1 ||
        az.dateAddAnalyze.toString().toLowerCase().indexOf(searchUsers.toLowerCase()) !== -1 ) {
        res.push(az);
      }
    }
    this.analizes = res;
    if (res.length === 0 ||
      !searchUsers) {
      this.getAnaliziesByUserId();
    }
  }

  pageChangeShowEntity(event) {
    this.size = event.target.value;
    this.requestTable.size = this.size;
    this.getAnaliziesByUserId();
  }

  pageChange(event) {
    this.page = event;
    this.requestTable.page = this.page;
    this.getAnaliziesByUserId();
  }


  private getAnaliziesByUserId() {
    const id = this.selectUser.id;
    this.profileService.getAllAnaliziesByUserIdPage(this.requestTable, id).subscribe(
      (response: ResponseTable<AnalyzeICDDate>) => {
        this.analizes = response.content;
        this.responseTable = response;
        if (this.analizes.length === 0) {
          this.notify.sendNotify(NotifyType.WARNING, 'You don\'t have analizies.');
          this.router.navigate(['/profile']);
        } else {
          this.notify.sendNotify(NotifyType.SUCCESS, `${this.analizes.length} analize(s) loaded successfully`);
        }
      },
      (error: HttpErrorResponse) => {
        console.log(error.message);
      }
    );
  }

  public handlePageChange(event) {
    this.page = event;
    this.requestTable.page = this.page;
    this.getAnaliziesByUserId();
  }

  public handlePageSizeChange(event) {
    this.size = event.target.value;
    this.page = 1;
    this.requestTable.size = this.size;
    this.requestTable.page = this.page;
    this.countSort = 3;
    this.getAnaliziesByUserId();
  }

  public sortByColumn($event) {
    this.countSort--;
    if (this.countSort === 0) {
      this.countSort = 3;
      this.sort = 'asc';
      this.activeColumn = '';
      this.getResetAnalizePageWithField();
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
      this.getAnaliziesByUserId();
    } else {
      this.sort = 'asc';
      this.page = 1;
      this.requestTable.column = this.column;
      this.requestTable.sort = this.sort;
      this.requestTable.page = this.page;
      this.getAnaliziesByUserId();
    }
  }

  private getRoleSession() {
    if (this.selectUser.role !== RoleUser.ROLE_USER) {
      this.flag = false;
    }
  }

  getResetAnalizePage() {
    this.requestTable.size = 5;
    this.requestTable.sort = 'desc';
    this.requestTable.column = 'date_add_analyze';
    this.activeColumn = '';
    this.countSort = 3;
    // @ts-ignore
    document.getElementById('changeValue').value ='5';
    this.getAnaliziesByUserId();
  }

  getResetAnalizePageWithField() {
    this.requestTable.sort = 'desc';
    this.requestTable.column = 'date_add_analyze';
    this.activeColumn = '';
    this.countSort = 3;
    this.getAnaliziesByUserId();
  }
  private loadUser() {
    const local = this.authService.getUserFromLocalCache();
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
