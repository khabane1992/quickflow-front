import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: '/derogation',
    pathMatch: 'full'
  },
  {
    path: 'derogation',
    loadChildren: () => import('./features/derogation/derogation.module').then(m => m.DerogationModule)
  },
  {
    path: '**',
    redirectTo: '/derogation'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }