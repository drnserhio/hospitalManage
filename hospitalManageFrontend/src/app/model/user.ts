import {ICD} from './ICD';
import {AnalyzeICDDate} from './analyzeICDDate';
import {Treatment} from './treatment';
import {Video} from './video';

export class User {
  public id: number;
  public userId: string;

  public firstname: string;
  public lastname: string;
  public patronomic: string;
  public age: number;
  public username: string;
  public password: string;
  public email: string;

  public profileImageUrl: string;
  public lastloginDate: Date;
  public joindDate: Date;

  public timeToVisitAt: Date;

  public address: string;
  public infoAboutComplaint: string; // жалобы role user
  public qrcode: string; // role user
  public infoAboutSick: string; // болезни перенесенные(role user )

  public infoDiagnosis: string; // диагноз ( чем а даный момент болен после исследования role doctror
  public treatment: Array<Treatment>; // лечение // change after visit treatment (doctor) чем лечить role doctor
  public hospiztalization: boolean; // role doctor

  public diagnosis: Array<AnalyzeICDDate>;

  public videoFiles: Array<Video>; // video files

  public role: string; // {ROLE_USER {read, edit}}, {ROLE_ADMIN {delete, update, create}}
  public authorities: [];
  public active: boolean;
  public notLocked: boolean;
  public online: boolean;

  constructor() {
    this.age = 0;
    this.userId = '';
    this.firstname = '';
    this.lastname = '';
    this.username = '';
    this.password = '';
    this.lastloginDate = null;
    this.joindDate = null;
    this.timeToVisitAt = null;
    this.profileImageUrl = '';
    this.email = '';
    this.address = '';
    this.infoAboutComplaint = '';
    this.infoDiagnosis = '';
    this.qrcode = '';
    this.infoAboutSick = '';
    this.hospiztalization = false;
    this.active = false;
    this.notLocked = false;
    this.online = false;
    this.role = '';
    this.authorities = [];
    this.diagnosis = [];
  }
}
