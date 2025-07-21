import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {
  DerogationRequestComponent
} from "./features/derogation/components/derogation-request/derogation-request.component";
import {MyRequestsComponent} from "./features/derogation/components/my-requests/my-requests.component";
import {DerogationManagementComponent} from "./features/derogation/components/derogation-management/derogation-management.component";

// Layout Components
import { SideMenuComponent, HeaderComponent } from './shared/layout';

@NgModule({
  declarations: [
    AppComponent,
    DerogationRequestComponent,
    MyRequestsComponent,
    DerogationManagementComponent,
    // Layout Components
    SideMenuComponent,
    HeaderComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
