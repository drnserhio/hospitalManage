import {Component, OnInit} from '@angular/core';
import {BehaviorSubject, Subscription} from 'rxjs';
import {NotifyService} from '../service/notify-service.service';
import {UserService} from '../service/user-service.service';
import {HttpErrorResponse} from '@angular/common/http';
import {User} from '../model/user';
import {NotifyType} from '../enum/notify-type.enum';
import {AuthService} from '../service/auth-service.service';
import {Router} from '@angular/router';
import {RoleUser} from '../enum/role-user.enum';
import {ProfileSaveHelper} from '../profile-select/profile-save-helper';
import {ResponseTable} from '../dto/response-table';
import {RequestTable} from '../dto/request-table';
import {Treatment} from '../model/treatment';


@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css'],
})
export class UserComponent implements OnInit {

  private loadIcon = true;
  private user: User;
  private sec: boolean;
  private selectedUser: User;
  private checkAdmin: boolean;

  users: User[] = [];
  private column = 'id';
  private sort = 'asc';
  private page = 1;
  private size = 5;
  pageSizes = [5, 10, 15, 20, 25, 30, 35];

  public requestTable = new RequestTable(this.column, this.sort, this.page, this.size);
  public responseTable: ResponseTable<User> = new ResponseTable<User>();
  private activeColumn: string;
  private countSort = 3;

  constructor(
    private userService: UserService,
    private notify: NotifyService,
    private authService: AuthService,
    private router: Router,
    private saveServiceUser: ProfileSaveHelper
  ) { }

  ngOnInit(): void {
    this.user = this.authService.getUserFromLocalCache();
    this.checkRole();
    if (this.user.role !== RoleUser.ROLE_USER) {
      this.sec = true;
      this.getUserPage();
    } else {
      this.router.navigate([`/profile`]);
    }
  }

  public getResetUserPage() {
    this.requestTable.size = 5;
    this.requestTable.sort = 'desc';
    this.requestTable.column = 'id';
    this.activeColumn = '';
    this.countSort = 3;
    // @ts-ignore
    document.getElementById('changeValue').value = '5'
    this.getUserPage();
  }

  public getResetUserPageWithfield() {
    this.requestTable.sort = 'desc';
    this.requestTable.column = 'id';
    this.activeColumn = '';
    this.countSort = 3;
    this.getUserPage();
  }

  public getUserPage() {
    this.userService.getUserPage(this.requestTable).subscribe(
      (response: ResponseTable<User>) => {
        this.users = response.content;
        this.responseTable = response;
        this.loadIcon = false;
        this.notification(NotifyType.SUCCESS, `${this.users.length} patien(s) loaded successfully`);
      },
      error => {
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
      this.getResetUserPageWithfield();
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
      this.getUserPage();
    } else {
      this.sort = 'asc';
      this.page = 1;
      this.requestTable.column = this.column;
      this.requestTable.sort = this.sort;
      this.requestTable.page = this.page;
      this.getUserPage();
    }
  }

  public handlePageChange(event) {
    this.page = event;
    this.requestTable.page = this.page;
    this.getUserPage();
  }

  public handlePageSizeChange(event) {
    console.log(event.target.value);
    this.size = event.target.value;
    this.page = 1;
    this.requestTable.size = this.size;
    this.requestTable.page = this.page;
    this.getUserPage();
  }

  public onSelected(selectUser: User): void {
    this.selectedUser = selectUser;
    this.saveServiceUser.setUserFromStorageUser(selectUser);
    this.router.navigate(['/selectprofile']);
  }

  public searchUsers(searchUsers: string): void {
    const res: User[] = [];
    for (const user of this.users) {
      if (user.firstname.toLowerCase().indexOf(searchUsers.toLowerCase()) !== -1 ||
        user.lastname.toLowerCase().indexOf(searchUsers.toLowerCase()) !== -1 ||
        user.email.toLowerCase().indexOf(searchUsers.toLowerCase()) !== -1 ||
        user.userId.toLowerCase().indexOf(searchUsers.toLowerCase()) !== -1) {
        res.push(user);
      }
    }
    this.users = res;
    if (res.length === 0 ||
      !searchUsers) {
      this.getUserPage();
    }
  }

  private checkRole(): void {
    if (this.user.role === RoleUser.ROLE_ADMIN ||
      this.user.role === RoleUser.ROLE_SUPER_ADMIN) {
      this.checkAdmin = true;
    } else {
      this.checkAdmin = false;
    }
  }

  public notification(notifyType: NotifyType, message: string): void {
    this.notify.sendNotify(notifyType, message);
  }

  public onToProfile(): void {
    this.router.navigate([`/profile`]);
  }

  pageChangeShowEntity(event) {
    this.size = event.target.value;
    this.requestTable.size = this.size;
    this.getUserPage();
  }

  pageChange(event) {
    this.page = event;
    this.requestTable.page = this.page;
    this.getUserPage();
  }
}
