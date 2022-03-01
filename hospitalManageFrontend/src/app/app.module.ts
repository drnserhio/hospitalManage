import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {AuthService} from './service/auth-service.service';
import {UserService} from './service/user-service.service';
import {LoginComponent} from './login/login.component';
import {RegisterComponent} from './register/register.component';
import {AppRoutingModule} from './app-routing.module';
import {ProfileUserComponent} from './profile-user/profile-user.component';
import {UserComponent} from './user/user.component';
import {FormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {NotifierService} from 'angular-notifier';
import {AuthInterceptor} from './interceptor/auth-interceptor';
import {AuthGuard} from './guard/auth-guard.guard';
import {NotificationModule} from './notification.module';
import {ProfileSelectComponent} from './profile-select/profile-select.component';
import {ProfileSelectOperationComponent} from './profile-select-operation/profile-select-operation.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgxPaginationModule} from 'ngx-pagination';
import {AnalizeCardComponent} from './analize-card-select/analize-card.component';
import {TreatmentCardComponent} from './treatment-card-select/treatment-card.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {TreatmentsCardUserProfileComponent} from './treatments-card-user-profile/treatments-card-user-profile.component';
import {AnalizeCardUserProfileComponent} from './analize-card-user-profile/analize-card-user-profile.component';
import {ChatComponent} from './chat/chat.component';
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    ProfileUserComponent,
    UserComponent,
    ProfileSelectComponent,
    ProfileSelectOperationComponent,
    AnalizeCardComponent,
    TreatmentCardComponent,
    TreatmentsCardUserProfileComponent,
    AnalizeCardUserProfileComponent,
    ChatComponent

  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        HttpClientModule,
        NotificationModule,
        BrowserAnimationsModule,
        NgxPaginationModule,
        NgbModule,
        MatProgressSpinnerModule
    ],
  providers: [AuthService, UserService, AuthGuard, NotifierService,
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
