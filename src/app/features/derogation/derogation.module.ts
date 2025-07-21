import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { DerogationFormComponent } from './components/derogation-form/derogation-form.component';

const routes: Routes = [
  {
    path: '',
    component: DerogationFormComponent
  },
  {
    path: 'dashboard',
    component: DerogationFormComponent
  },
  {
    path: 'nouvelle-demande',
    component: DerogationFormComponent
  },
  {
    path: 'edit/:id',
    component: DerogationFormComponent
  }
];

@NgModule({
  declarations: [
    DerogationFormComponent,
    DerogationFormComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes)
  ]
})
export class DerogationModule { }