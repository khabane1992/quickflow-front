import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {
  DerogationRequestComponent
} from "./features/derogation/components/derogation-request/derogation-request.component";
import {MyRequestsComponent} from "./features/derogation/components/my-requests/my-requests.component";
import {DerogationManagementComponent} from "./features/derogation/components/derogation-management/derogation-management.component";

// Layout Components
import { SideMenuComponent, HeaderComponent } from './shared/layout';

// Mock API
import { MockApiInterceptor } from './core/interceptors/mock-api.interceptor';
import { MockApiService } from './core/mock/mock-api.service';

// Debug Panel
import { ApiDebugPanelComponent } from './shared/components/api-debug-panel/api-debug-panel.component';

// Notifications
import { NotificationToastComponent } from './shared/components/notification-toast/notification-toast.component';
import { NotificationService } from './core/services/notification.service';

@NgModule({
  declarations: [
    AppComponent,
    DerogationRequestComponent,
    MyRequestsComponent,
    DerogationManagementComponent,
    // Layout Components
    SideMenuComponent,
    HeaderComponent,
    // Debug Components
    ApiDebugPanelComponent,
    // Notification Components
    NotificationToastComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    HttpClientModule
  ],
  providers: [
    MockApiService,
    NotificationService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: MockApiInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
