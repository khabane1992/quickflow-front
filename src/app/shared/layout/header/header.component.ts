import { Component, EventEmitter, Output } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {
  @Output() sidebarToggle = new EventEmitter<void>();

  constructor(protected router: Router) {}

  toggleSidebar() {
    this.sidebarToggle.emit();
  }

  getCurrentPageTitle(): string {
    const route = this.router.url;
    if (route === '/mes-demandes') {
      return 'Mes demandes';
    } else if (route === '/demande-derogation') {
      return 'Dérogation tarifaire';
    } else if (route === '/nouvelle-demande') {
      return 'Nouvelle demande de dérogation tarifaire';
    } else {
      return 'QlickFlow';
    }
  }

  getCurrentBreadcrumb(): string {
    const route = this.router.url;
    if (route === '/mes-demandes') {
      return 'Qlickflow / Mes demandes';
    } else if (route === '/demande-derogation') {
      return 'Qlickflow / Dérogation tarifaire';
    } else if (route === '/nouvelle-demande') {
      return 'Qlickflow / Nouvelle demande';
    } else {
      return 'Qlickflow';
    }
  }
}
