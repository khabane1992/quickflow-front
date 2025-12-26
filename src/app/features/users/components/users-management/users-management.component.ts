import { Component, OnInit } from '@angular/core';

interface User {
  id: number;
  prenom: string;
  nom: string;
  uid: string;
  profil: string;
  profileBackup: string;
  affectation: string;
  actif: boolean;
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
    console.log('Ajouter un nouvel utilisateur');
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
