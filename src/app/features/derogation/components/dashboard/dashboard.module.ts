import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DerogationDashboardComponent } from './dashboard.component';  // ← Bon nom

@NgModule({
  declarations: [
    DerogationDashboardComponent  // ← Bon nom au lieu de DashboardComponent
  ],
  imports: [
    CommonModule
  ],
  exports: [
    DerogationDashboardComponent  // ← Bon nom au lieu de DashboardComponent
  ]
})
export class DashboardModule { }