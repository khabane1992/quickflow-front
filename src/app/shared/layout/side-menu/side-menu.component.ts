import {Component} from '@angular/core';
import {Router} from '@angular/router';

@Component({
    selector: 'app-side-menu',
    templateUrl: './side-menu.component.html',
    styleUrls: ['./side-menu.component.scss']
})
export class SideMenuComponent {
    sidebarOpen = false;

    constructor(protected router: Router) {
    }

    navigateTo(route: string) {
        this.router.navigate([route]);
        // Close sidebar on mobile after navigation
        if (window.innerWidth <= 768) {
            this.sidebarOpen = false;
        }
    }

    toggleSidebar() {
        this.sidebarOpen = !this.sidebarOpen;
    }

    onHelpConsult() {
        console.log('Opening help guide...');
        // Implement help guide logic here
    }
}
