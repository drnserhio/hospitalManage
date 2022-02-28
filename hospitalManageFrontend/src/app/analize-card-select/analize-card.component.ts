import {Component, OnInit} from '@angular/core';
import {RequestTable} from '../dto/request-table';
import {ResponseTable} from '../dto/response-table';
import {User} from '../model/user';
import {NotifyService} from '../service/notify-service.service';
import {Router} from '@angular/router';
import {ProfileSaveHelper} from '../profile-select/profile-save-helper';
import {ProfileService} from '../service/profile-service.service';
import {NotifyType} from '../enum/notify-type.enum';
import {HttpErrorResponse} from '@angular/common/http';
import {AnalyzeICDDate} from "../model/analyzeICDDate";

@Component({
  selector: 'app-analize-card',
  templateUrl: './analize-card.component.html',
  styleUrls: ['./analize-card.component.css']
})
export class AnalizeCardComponent implements OnInit {

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
              private saveService: ProfileSaveHelper,
              private profileService: ProfileService) { }

  ngOnInit() {
    this.selectUser = this.saveService.getFromStorageUser();
    this.getAnaliziesByUserId();
  }


  public searchAnalize(searchUsers: string): void {
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

  getAnaliziesByUserId() {
    this.profileService.getAllAnaliziesByUserIdPage(this.requestTable, this.selectUser.id).subscribe(
      (response: ResponseTable<AnalyzeICDDate>) => {
        this.analizes =  response.content;
        this.responseTable = response;
        if (this.analizes.length === 0) {
          this.notify.sendNotify(NotifyType.WARNING, 'This patient don\'t have analizies.');
          this.router.navigate(['/selectprofile']);
        } else {
          this.notify.sendNotify(NotifyType.SUCCESS, `${this.analizes.length} analize(s) loaded successfully`);
        }
      },
      (error ) => {
        console.log(error.message);
      }
    );
  }

  getResetAnalizies() {
    this.requestTable.size = 5;
    this.requestTable.sort = 'desc';
    this.requestTable.column = 'date_add_analyze';
    this.activeColumn = '';
    // @ts-ignore
    document.getElementById('changeValue').value = '5';
    this.getAnaliziesByUserId();
  }

  getResetAnaliziesWithField() {
    this.requestTable.sort = 'desc';
    this.requestTable.column = 'date_add_analyze';
    this.activeColumn = '';
    this.getAnaliziesByUserId();
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
    this.getAnaliziesByUserId();
  }

  public sortByColumn($event) {
    this.countSort--;
    if (this.countSort === 0) {
      this.countSort = 3;
      this.sort = 'asc';
      this.activeColumn = '';
      this.getResetAnaliziesWithField();
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

  onDelete(anailzeId: number) {
    this.profileService.deleteAnalizeFromUser(this.selectUser.id, anailzeId).subscribe(
      (response: boolean) => {
        if (response) {
          this.notify.sendNotify(NotifyType.SUCCESS, 'You successfull delete analize');
          this.getAnaliziesByUserId();
        }
      },
      (error: HttpErrorResponse) => {
        this.notify.sendNotify(NotifyType.WARNING, 'You can\'t delete analize');
      }
    );
  }
}
