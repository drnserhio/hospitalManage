import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpEvent, HttpEventType} from '@angular/common/http';
import {Router} from '@angular/router';
import {UserService} from '../service/user-service.service';
import {ProfileSaveHelper} from '../profile-select/profile-save-helper';
import {User} from '../model/user';
import {NotifyService} from '../service/notify-service.service';
import {NotifyType} from '../enum/notify-type.enum';
import {VideoService} from '../service/video.service';
import {saveAs} from 'file-saver';
import {ConstError} from "../enum/const-error-enum";
import {ResponseTable} from "../dto/response-table";
import {Video} from "../model/video";
import {RequestTable} from "../dto/request-table";

@Component({
  selector: 'app-profile-select-operation',
  templateUrl: './profile-select-operation.component.html',
  styleUrls: ['./profile-select-operation.component.css']
})
export class ProfileSelectOperationComponent implements OnInit {
  filenames: string[] = [];
  fileStatus = {status: '', requestType: '', percent: 0};

  select: User;
  public load = true;

  private column = 'create_date';
  private sort = 'asc';
  private page = 1;
  private size = 5;
  pageSizes = [5, 10, 15, 20, 25, 30, 35];
  public requestTable = new RequestTable(this.column, this.sort, this.page, this.size);
  public responseTable: ResponseTable<Video> = new ResponseTable<Video>();
  private videoFiles: Video[] = [];
  private countSort = 3;
  private flagSort = true;

  constructor(
    private httpClient: HttpClient,
    private router: Router,
    private userService: UserService,
    private selectSaveUser: ProfileSaveHelper,
    private videoService: VideoService,
    private notify: NotifyService,
    private fileService: VideoService) { }

  ngOnInit() {
    this.select = this.selectSaveUser.getFromStorageUser();
    this.getVideoPage();
  }

  public onToUsersList() {
    this.router.navigate([`/user/home`]);
  }

  public onToSelectProfile() {
    this.router.navigate([`/selectprofile`]);
  }


  private clickButtonId(buttonId: string) {
    document.getElementById(buttonId).click();
  }

  public onUploadFiles(files: File[]): void {
    const formData = new FormData();
    for (const file of files) { formData.append('files', file, file.name); }
    formData.forEach(e => console.log(e));
    this.fileService.upload(this.select.username, formData).subscribe(
      event => {
        console.log(event);
        this.resportProgress(event);
      },
      (error: HttpErrorResponse) => {
        console.log(error);
      }
    );
  }

  public onDownloadFiles(fileName: string): void {
    this.fileService.download(this.select.username, fileName).subscribe(
      event => {
        this.resportProgress(event);
      },
      (error: HttpErrorResponse) => {
        console.log(error);
        this.notify.sendNotify(NotifyType.WARNING, ConstError.DENIED_DOWNLOAD_FILE);
      }
    );
  }

  public onRemoveFile(filename: string): void {
    this.fileService.removeFileInSetUser(this.select.username, filename).subscribe(
      (response: User) => {
        this.select = response;
        this.selectSaveUser.addSelecteUserToStorage(response);
        this.getResetVideoPage();
        this.notify.sendNotify(NotifyType.SUCCESS, 'Your video successfully remove in server.');

      },
      (error: HttpErrorResponse) => {
        this.notify.sendNotify(NotifyType.WARNING, ConstError.DENIED_REMOVE_FILE);
      }
    );
  }


  private resportProgress(httpEvent: HttpEvent<string[] | Blob>): void {
    switch (httpEvent.type) {
      case HttpEventType.UploadProgress:
        this.updateStatus(httpEvent.loaded, httpEvent.total, 'Uploading... ');
        break;
      case HttpEventType.DownloadProgress:
        this.updateStatus(httpEvent.loaded, httpEvent.total, 'Downloading... ');
        break;
      case HttpEventType.ResponseHeader:
        console.log('Header returned', httpEvent);
        break;
      case HttpEventType.Response:
        if (httpEvent.body instanceof Array) {
          for (const fileName of httpEvent.body) {
            this.filenames.unshift(fileName);
          }
        } else {
          saveAs(new File([httpEvent.body], httpEvent.headers.get('File-Name'),
            {type: `${httpEvent.headers.get('Content-Type')};charset=utf-8`}));
        }
        break;
      default:
        console.log(httpEvent);
    }
  }

  private updateStatus(loaded: number, total: number, requestType: string) {
    this.fileStatus.status = 'progress';
    this.fileStatus.requestType = requestType;
    this.fileStatus.percent = Math.round(100 * loaded / total);
  }

  public onSaveChange() {
    this.userService.findUser(this.select.username).subscribe(
      (response: User) => {
        this.select = response;
        this.selectSaveUser.addSelecteUserToStorage(response);
        this.getResetVideoPage();
        this.clickButtonId('uploadVideo');

      },
      (error: HttpErrorResponse) => {
      this.notify.sendNotify(NotifyType.WARNING, 'Error server.');
      }
    );
  }

  downloadDocument(): void {
    this.userService.getDocument(this.select.username).subscribe(
        (response: File) => {
          console.log(response)
          let saveAs1 = saveAs(response, this.select.firstname + ' ' + this.select.lastname + new Date().toDateString() + '.docx');
          if (saveAs1) {
           this.onProgressBarDownloadOff()
          }
          this.notify.sendNotify(NotifyType.INFO, 'Your document successful download.');
        },
        (error: HttpErrorResponse) => {
          this.notify.sendNotify(NotifyType.WARNING, ConstError.DENIED_DOWNLOAD_FILE);
          console.log(error.error.message)
        }
    );
  }

  public handlePageChange(event) {
    this.page = event;
    this.requestTable.page = this.page;
    this.getVideoPage();
  }

  public handlePageSizeChange(event) {
    this.size = event.target.value;
    this.page = 1;
    this.requestTable.size = this.size;
    this.requestTable.page = this.page;
    this.flagSort = true
    this.countSort = 3;
    this.getVideoPage();
  }

  pageChange(event) {
    this.page = event;
    this.requestTable.page = this.page;
    this.getVideosByUserIdWIthNewField();
  }

  private getVideoPage() {
    this.userService.getVideosPageByUserId(this.requestTable, this.select.id).subscribe(
      (response : ResponseTable<Video>) => {
        this.videoFiles = response.content;
        this.responseTable = response;
        if (this.videoFiles.length === 0) {
          this.notify.sendNotify(NotifyType.WARNING, 'This patient don\'t have videos.');
        } else {
          this.notify.sendNotify(NotifyType.SUCCESS, `User: ${this.select.username} has ${this.videoFiles.length} videos.`)
        }
      },
      (error: HttpErrorResponse) => {
        console.log(error.message);
      }
    )
  }

  public sortByColumn($event) {
    this.countSort--;
    if (this.countSort === 0) {
      this.countSort = 3;
      this.sort = '';
      this.flagSort = true;
      this.getVideoPage();
      return;
    }
    // @ts-ignore
    this.column = event.srcElement.id;
    if (this.sort.endsWith('asc')) {
      this.flagSort = false;
      this.sort = 'desc';
      this.page = 1;
      this.requestTable.sort = this.sort;
      this.requestTable.column = this.column;
      this.requestTable.page = this.page;
      this.getVideosByUserIdWIthNewField();
    } else {
      this.flagSort = false;
      this.sort = 'asc';
      this.page = 1;
      this.requestTable.column = this.column;
      this.requestTable.sort = this.sort;
      this.requestTable.page = this.page;
      this.getVideosByUserIdWIthNewField();
    }
  }

  private getVideosByUserIdWIthNewField() {
    const id = this.select.id;
    this.userService.getVideosPageByUserId(this.requestTable, id).subscribe(
      (response: ResponseTable<Video>) => {
        this.videoFiles = response.content;
        this.responseTable = response;
        if (this.videoFiles.length === 0) {
          this.notify.sendNotify(NotifyType.WARNING, 'This patient don\'t have videos.');
        } else {
          this.notify.sendNotify(NotifyType.SUCCESS, `User: ${this.select.username} has ${this.videoFiles.length} videos.`)
        }
      },
      (error: HttpErrorResponse) => {
        console.log(error.message);
      }
    );
  }

  public getResetVideoPage() {
    this.requestTable.size = 5;
    this.requestTable.sort = 'desc';
    this.requestTable.column = 'create_date';
    this.flagSort = true
    this.countSort = 3;
    this.getVideoPage();
  }

  onProgressBarDownloadOn() {
   this.load = true;
  }
  onProgressBarDownloadOff() {
    this.load = false;
  }
}
