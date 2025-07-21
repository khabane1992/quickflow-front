// my-requests.component.ts
import { Component, OnInit } from '@angular/core';

interface DerogationRequest {
  id: string;
  clientName: string;
  requestType: string;
  submissionDate: Date;
  status: 'pending' | 'approved' | 'rejected' | 'draft';
  amount?: number;
  currency?: string;
}

@Component({
  selector: 'app-my-requests',
  templateUrl: './my-requests.component.html',
  styleUrls: ['./my-requests.component.scss']
})
export class MyRequestsComponent implements OnInit {
  requests: DerogationRequest[] = [
    {
      id: 'REQ-001',
      clientName: 'Client ABC Corp',
      requestType: 'Dérogation tarifaire',
      submissionDate: new Date('2025-07-15'),
      status: 'pending',
      amount: 15000,
      currency: 'EUR'
    },
    {
      id: 'REQ-002',
      clientName: 'Client XYZ Ltd',
      requestType: 'Dérogation tarifaire',
      submissionDate: new Date('2025-07-12'),
      status: 'approved',
      amount: 8500,
      currency: 'EUR'
    },
    {
      id: 'REQ-003',
      clientName: 'Client DEF Inc',
      requestType: 'Dérogation tarifaire',
      submissionDate: new Date('2025-07-10'),
      status: 'rejected',
      amount: 12000,
      currency: 'EUR'
    },
    {
      id: 'REQ-004',
      clientName: 'Client GHI SA',
      requestType: 'Dérogation tarifaire',
      submissionDate: new Date('2025-07-08'),
      status: 'draft'
    }
  ];

  filteredRequests: DerogationRequest[] = [];
  selectedStatus = '';
  selectedPeriod = '';
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  constructor() { }

  ngOnInit(): void {
    this.filterRequests();
  }

  filterRequests() {
    this.filteredRequests = this.requests.filter(request => {
      const statusMatch = !this.selectedStatus || request.status === this.selectedStatus;
      const periodMatch = this.checkPeriodMatch(request.submissionDate);
      return statusMatch && periodMatch;
    });

    this.totalPages = Math.ceil(this.filteredRequests.length / this.itemsPerPage);
    this.currentPage = 1;
  }

  checkPeriodMatch(date: Date): boolean {
    if (!this.selectedPeriod) return true;

    const now = new Date();
    const requestDate = new Date(date);

    switch (this.selectedPeriod) {
      case 'week':
        const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
        return requestDate >= weekAgo;
      case 'month':
        const monthAgo = new Date(now.getFullYear(), now.getMonth() - 1, now.getDate());
        return requestDate >= monthAgo;
      case 'quarter':
        const quarterAgo = new Date(now.getFullYear(), now.getMonth() - 3, now.getDate());
        return requestDate >= quarterAgo;
      default:
        return true;
    }
  }

  getStatusLabel(status: string): string {
    const labels: {[key: string]: string} = {
      'pending': 'En cours',
      'approved': 'Approuvée',
      'rejected': 'Refusée',
      'draft': 'Brouillon'
    };
    return labels[status] || status;
  }

  viewRequest(id: string) {
    console.log('Viewing request:', id);
    // Navigate to view request details
  }

  editRequest(id: string) {
    console.log('Editing request:', id);
    // Navigate to edit request
  }

  deleteRequest(id: string) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette demande?')) {
      this.requests = this.requests.filter(req => req.id !== id);
      this.filterRequests();
    }
  }

  changePage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }
}
