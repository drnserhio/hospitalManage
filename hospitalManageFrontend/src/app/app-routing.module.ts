import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {RegisterComponent} from './register/register.component';
import {UserComponent} from './user/user.component';
import {ProfileUserComponent} from './profile-user/profile-user.component';
import {AuthGuard} from './guard/auth-guard.guard';
import {ProfileSelectComponent} from './profile-select/profile-select.component';
import {ProfileSelectOperationComponent} from './profile-select-operation/profile-select-operation.component';
import {TreatmentCardComponent} from './treatment-card-select/treatment-card.component';
import {AnalizeCardComponent} from './analize-card-select/analize-card.component';
import {
  TreatmentsCardUserProfileComponent
} from './treatments-card-user-profile/treatments-card-user-profile.component';
import {AnalizeCardUserProfileComponent} from './analize-card-user-profile/analize-card-user-profile.component';
import {ChatComponent} from './chat/chat.component';


const routes: Routes = [
  {path: '', redirectTo: '/login', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'user/home', component: UserComponent , canActivate: [AuthGuard]},
  {path: 'profile', component: ProfileUserComponent, canActivate: [AuthGuard]},
  {path: 'selectprofile', component: ProfileSelectComponent, canActivate: [AuthGuard]},
  {path: 'operation/info', component: ProfileSelectOperationComponent, canActivate: [AuthGuard]},
  {path: 'analize/card', component: AnalizeCardComponent, canActivate: [AuthGuard]},
  {path: 'analize/card/profile', component: AnalizeCardUserProfileComponent, canActivate: [AuthGuard]},
  {path: 'treatments/card', component: TreatmentCardComponent, canActivate: [AuthGuard]},
  {path: 'treatments/profile/card', component: TreatmentsCardUserProfileComponent, canActivate: [AuthGuard]},
  {path: 'chat', component: ChatComponent, canActivate: [AuthGuard]}
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule { }
