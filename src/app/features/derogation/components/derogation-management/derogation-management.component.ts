import { Component, OnInit } from '@angular/core';

interface DerogationRequest {
  id: string;
  dateReception: string;
  type: string;
  clientName: string;
  sabId: string;
  owner: string;
  responsable: string;
  entity: string;
  status: 'pending' | 'approved' | 'rejected' | 'draft' | 'processing';
  dateCloture?: string;
}

@Component({
  selector: 'app-derogation-management',
  templateUrl: './derogation-management.component.html',
  styleUrls: ['./derogation-management.component.scss']
})
export class DerogationManagementComponent implements OnInit {

  requestsToProcess: DerogationRequest[] = [
    {
      id: 'REQ-001',
      dateReception: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Amine TALBI',
      sabId: '8939782',
      owner: 'Ghita ALAMI',
      responsable: 'Halima SAKHI',
      entity: 'Agence MED V Casablanca',
      status: 'processing'
    },
    {
      id: 'REQ-002',
      dateReception: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Khalid Benslimane',
      sabId: '3893921',
      owner: 'Ghita ALAMI',
      responsable: 'Halima SAKHI',
      entity: 'Agence MED V Casablanca',
      status: 'processing'
    },
    {
      id: 'REQ-003',
      dateReception: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Ghita HAJJI',
      sabId: '8189739',
      owner: 'Ghita ALAMI',
      responsable: 'Youssef BELKHIR',
      entity: 'Agence MED V Casablanca',
      status: 'processing'
    },
    {
      id: 'REQ-004',
      dateReception: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Meriem KHALDOUNE',
      sabId: '9282819',
      owner: 'Ghita ALAMI',
      responsable: 'Khalid NAJJI',
      entity: 'Agence MED V Casablanca',
      status: 'processing'
    },
    {
      id: 'REQ-005',
      dateReception: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Touria NAJJI',
      sabId: '6372728',
      owner: 'Ghita ALAMI',
      responsable: 'Youssef BELKHIR',
      entity: 'Agence MED V Casablanca',
      status: 'draft'
    },
    {
      id: 'REQ-006',
      dateReception: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Karim BENHAMMOU',
      sabId: '3291838',
      owner: 'Ghita ALAMI',
      responsable: 'Khalid NAJJI',
      entity: 'Agence MED V Casablanca',
      status: 'processing'
    }
  ];

  requestsPendingValidation: DerogationRequest[] = [
    {
      id: 'REQ-007',
      dateReception: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Amine TALBI',
      sabId: '8939782',
      owner: 'Ghita ALAMI',
      responsable: 'Halima SAKHI',
      entity: 'Agence MED V Casablanca',
      status: 'pending'
    },
    {
      id: 'REQ-008',
      dateReception: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Khalid Benslimane',
      sabId: '3893921',
      owner: 'Ghita ALAMI',
      responsable: 'Halima SAKHI',
      entity: 'Agence MED V Casablanca',
      status: 'pending'
    },
    {
      id: 'REQ-009',
      dateReception: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Ghita HAJJI',
      sabId: '8189739',
      owner: 'Ghita ALAMI',
      responsable: 'Youssef BELKHIR',
      entity: 'Agence MED V Casablanca',
      status: 'pending'
    },
    {
      id: 'REQ-010',
      dateReception: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Meriem KHALDOUNE',
      sabId: '9282819',
      owner: 'Ghita ALAMI',
      responsable: 'Khalid NAJJI',
      entity: 'Agence MED V Casablanca',
      status: 'pending'
    }
  ];

  requestsProcessed: DerogationRequest[] = [
    {
      id: 'REQ-011',
      dateReception: '07/07/2025',
      dateCloture: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Amine TALBI',
      sabId: '8939782',
      owner: 'Ghita ALAMI',
      responsable: '',
      entity: 'Agence MED V Casablanca',
      status: 'approved'
    },
    {
      id: 'REQ-012',
      dateReception: '07/07/2025',
      dateCloture: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Khalid Benslimane',
      sabId: '3893921',
      owner: 'Ghita ALAMI',
      responsable: '',
      entity: 'Agence MED V Casablanca',
      status: 'approved'
    },
    {
      id: 'REQ-013',
      dateReception: '07/07/2025',
      dateCloture: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Ghita HAJJI',
      sabId: '8189739',
      owner: 'Ghita ALAMI',
      responsable: '',
      entity: 'Agence MED V Casablanca',
      status: 'approved'
    },
    {
      id: 'REQ-014',
      dateReception: '07/07/2025',
      dateCloture: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Meriem KHALDOUNE',
      sabId: '9282819',
      owner: 'Ghita ALAMI',
      responsable: '',
      entity: 'Agence MED V Casablanca',
      status: 'approved'
    },
    {
      id: 'REQ-015',
      dateReception: '07/07/2025',
      dateCloture: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Touria NAJJI',
      sabId: '6372728',
      owner: 'Ghita ALAMI',
      responsable: '',
      entity: 'Agence MED V Casablanca',
      status: 'rejected'
    },
    {
      id: 'REQ-016',
      dateReception: '07/07/2025',
      dateCloture: '07/07/2025',
      type: 'Dérogation tarifaire',
      clientName: 'Karim BENHAMMOU',
      sabId: '3291838',
      owner: 'Ghita ALAMI',
      responsable: '',
      entity: 'Agence MED V Casablanca',
      status: 'approved'
    }
  ];

  constructor() { }

  ngOnInit(): void {
  }

  processRequest(request: DerogationRequest) {
    console.log('Processing request:', request);
    // Implement process logic
  }

  validateRequest(request: DerogationRequest) {
    console.log('Validating request:', request);
    // Implement validation logic
  }

  viewRequest(request: DerogationRequest) {
    console.log('Viewing request:', request);
    // Implement view logic
  }

  getStatusLabel(status: string): string {
    const labels: {[key: string]: string} = {
      'pending': 'En attente',
      'approved': 'Accordée',
      'rejected': 'Refusée',
      'draft': 'Brouillons',
      'processing': 'À traiter'
    };
    return labels[status] || status;
  }

  getStatusClass(status: string): string {
    const classes: {[key: string]: string} = {
      'pending': 'status-pending',
      'approved': 'status-approved',
      'rejected': 'status-rejected',
      'draft': 'status-draft',
      'processing': 'status-processing'
    };
    return classes[status] || '';
  }

  getInitials(name: string): string {
    return name.split(' ').map(n => n.charAt(0)).join('').toUpperCase();
  }
}
