import { Component, OnInit } from '@angular/core';

interface User {
  id: number;
  prenom: string;
  nom: string;
  uid: string;
  email?: string;
  profil: string;
  profileBackup: string;
  affectation: string;
  actif: boolean;
}

interface Profile {
  id: string;
  name: string;
}

@Component({
  selector: 'app-users-management',
  templateUrl: './users-management.component.html',
  styleUrls: ['./users-management.component.scss']
})
export class UsersManagementComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  searchTerm: string = '';
  activeTab: 'qlickflow' | 'console' = 'console';

  // Pagination
  currentPage: number = 1;
  itemsPerPage: number = 4;
  totalPages: number = 1;

  // Modal d'ajout d'utilisateur
  isAddUserModalOpen: boolean = false;
  selectedProfile: string = '';
  newUser: Partial<User> = {};

  availableProfiles: Profile[] = [
    { id: 'SA', name: 'Super Admin (SA)' },
    { id: 'ADM', name: 'Administrateur (ADM)' },
    { id: 'JUR', name: 'Juridique (JUR)' },
    { id: 'USR', name: 'Utilisateur (USR)' }
  ];

  ngOnInit(): void {
    this.loadMockData();
  }

  loadMockData(): void {
    // Données de démonstration basées sur les maquettes
    this.users = [
      { id: 1, prenom: 'John', nom: 'DOE', uid: 'CXX', profil: 'JUR', profileBackup: 'P.B', affectation: 'Agence', actif: true },
      { id: 2, prenom: 'John', nom: 'DOE', uid: 'CXX', profil: 'JUR', profileBackup: 'P.B', affectation: 'Agence', actif: true },
      { id: 3, prenom: 'John', nom: 'DOE', uid: 'CXX', profil: 'JUR', profileBackup: 'P.B', affectation: 'Agence', actif: true },
      { id: 4, prenom: 'John', nom: 'DOE', uid: 'CXX', profil: 'JUR', profileBackup: 'P.B', affectation: 'Agence', actif: true },
      { id: 5, prenom: 'Jane', nom: 'SMITH', uid: 'DYY', profil: 'ADM', profileBackup: 'P.A', affectation: 'Siege', actif: false },
      { id: 6, prenom: 'Marc', nom: 'DUPONT', uid: 'EZZ', profil: 'USR', profileBackup: 'P.C', affectation: 'Agence', actif: true },
      { id: 7, prenom: 'Sophie', nom: 'MARTIN', uid: 'FWW', profil: 'JUR', profileBackup: 'P.B', affectation: 'Agence', actif: true },
      { id: 8, prenom: 'Pierre', nom: 'BERNARD', uid: 'GVV', profil: 'ADM', profileBackup: 'P.A', affectation: 'Siege', actif: false },
    ];
    this.applyFilter();
  }

  applyFilter(): void {
    if (!this.searchTerm.trim()) {
      this.filteredUsers = [...this.users];
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredUsers = this.users.filter(user =>
        user.prenom.toLowerCase().includes(term) ||
        user.nom.toLowerCase().includes(term) ||
        user.uid.toLowerCase().includes(term) ||
        user.profil.toLowerCase().includes(term) ||
        user.affectation.toLowerCase().includes(term)
      );
    }
    this.totalPages = Math.ceil(this.filteredUsers.length / this.itemsPerPage);
    this.currentPage = 1;
  }

  onSearch(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.searchTerm = target.value;
    this.applyFilter();
  }

  setActiveTab(tab: 'qlickflow' | 'console'): void {
    this.activeTab = tab;
  }

  toggleUserStatus(user: User): void {
    user.actif = !user.actif;
  }

  editUser(user: User): void {
    console.log('Editer utilisateur:', user);
  }

  deleteUser(user: User): void {
    console.log('Supprimer utilisateur:', user);
  }

  addUser(): void {
    this.openAddUserModal();
  }

  openAddUserModal(): void {
    this.selectedProfile = '';
    this.newUser = {};
    this.isAddUserModalOpen = true;
    document.body.style.overflow = 'hidden';
  }

  closeAddUserModal(): void {
    this.isAddUserModalOpen = false;
    this.selectedProfile = '';
    this.newUser = {};
    document.body.style.overflow = 'auto';
  }

  selectProfile(profileId: string): void {
    this.selectedProfile = profileId;
  }

  getSelectedProfileName(): string {
    if (!this.selectedProfile) {
      return 'Choisir';
    }
    const profile = this.availableProfiles.find(p => p.id === this.selectedProfile);
    return profile ? profile.name : 'Choisir';
  }

  saveNewUser(): void {
    if (!this.selectedProfile) {
      alert('Veuillez sélectionner un profil');
      return;
    }

    if (!this.newUser.uid || !this.newUser.prenom || !this.newUser.nom) {
      alert('Veuillez remplir tous les champs obligatoires');
      return;
    }

    const newId = Math.max(...this.users.map(u => u.id)) + 1;
    const user: User = {
      id: newId,
      prenom: this.newUser.prenom || '',
      nom: this.newUser.nom || '',
      uid: this.newUser.uid || '',
      profil: this.selectedProfile,
      profileBackup: 'P.B',
      affectation: 'Agence',
      actif: true
    };

    this.users.push(user);
    this.applyFilter();
    this.closeAddUserModal();
  }

  get paginatedUsers(): User[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredUsers.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  trackByUserId(index: number, user: User): number {
    return user.id;
  }
}



<div class="pagination">
  <button 
    class="nav-btn" 
    (click)="goToPage(currentPage - 1)" 
    [disabled]="currentPage === 1">
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <polyline points="15,18 9,12 15,6"></polyline>
    </svg>
  </button>

  <div class="page-numbers">
    <ng-container *ngFor="let page of pageNumbers">
      <button 
        class="page-btn" 
        [class.active]="page === currentPage"
        (click)="goToPage(page)">
        {{ page }}
      </button>
    </ng-container>
  </div>

  <button 
    class="nav-btn" 
    (click)="goToPage(currentPage + 1)" 
    [disabled]="currentPage === totalPages">
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <polyline points="9,18 15,12 9,6"></polyline>
    </svg>
  </button>
</div>

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 24px;
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.nav-btn,
.page-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.nav-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #00965E 0%, #00c47d 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(0, 150, 94, 0.3);
}

.nav-btn:hover:not(:disabled) {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(0, 150, 94, 0.4);
}

.nav-btn:disabled {
  background: #e0e0e0;
  color: #999;
  cursor: not-allowed;
  box-shadow: none;
}

.page-btn {
  min-width: 40px;
  height: 40px;
  border-radius: 12px;
  background: #f5f5f5;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.page-btn:hover:not(.active) {
  background: #e8f5e9;
  color: #00965E;
}

.page-btn.active {
  background: linear-gradient(135deg, #00965E 0%, #00c47d 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(0, 150, 94, 0.3);
  transform: scale(1.05);
}
      
