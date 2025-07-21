import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MyRequestsComponent} from "./features/derogation/components/my-requests/my-requests.component";
import {
  DerogationRequestComponent
} from "./features/derogation/components/derogation-request/derogation-request.component";
import {DerogationManagementComponent} from "./features/derogation/components/derogation-management/derogation-management.component";

const routes: Routes = [
  { path: '', redirectTo: '/mes-demandes', pathMatch: 'full' },
  { path: 'mes-demandes', component: MyRequestsComponent },
  { path: 'demande-derogation', component: DerogationManagementComponent },
  { path: 'nouvelle-demande', component: DerogationRequestComponent },
  { path: '**', redirectTo: '/mes-demandes' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
