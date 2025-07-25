// src/app/core/mock/mock-api.service.ts

import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { 
  DashboardRequestDTO, 
  ApiResponse, 
  PageResponse,
  DerogationStatus,
  DerogationType 
} from '../../shared/interfaces/derogation-mangement.interface';

@Injectable({
  providedIn: 'root'
})
export class MockApiService {

  private mockData: DashboardRequestDTO[] = [
    {
      id: 1,
      createdAt: '15/01/2025',
      requestType: 'Navigation logiciels',
      clientName: 'Société ABC Corp',
      sabClientId: 'SAB001234',
      owner: 'Anna Aiello',
      responsable: 'Anna Aiello',
      agency: 'Agence Paris',
      proposedStatus: 'En cours de traitement',
      status: DerogationStatus.PENDING,
      derogationType: DerogationType.TARIFF,
      derogationValue: 15000,
      proposedEffectiveDate: '20/01/2025',
      proposedEndDate: '20/07/2025',
      daysPending: 3,
      actions: 'Traiter'
    },
    {
      id: 2,
      createdAt: '14/01/2025',
      requestType: 'Droit de diffusion',
      clientName: 'Entreprise XYZ Ltd',
      sabClientId: 'SAB005678',
      owner: 'Marc Dupont',
      responsable: 'Sophie Martin',
      agency: 'Agence Lyon',
      proposedStatus: 'En attente de validation',
      status: DerogationStatus.IN_REVIEW,
      derogationType: DerogationType.COMMISSION,
      derogationValue: 8500,
      proposedEffectiveDate: '25/01/2025',
      proposedEndDate: '25/04/2025',
      daysPending: 2,
      actions: 'Valider'
    },
    {
      id: 3,
      createdAt: '13/01/2025',
      requestType: 'Dérogation de limite',
      clientName: 'Client DEF Inc',
      sabClientId: 'SAB009876',
      owner: 'Julie Bernard',
      responsable: 'Anna Aiello',
      agency: 'Agence Marseille',
      proposedStatus: 'Approuvée',
      status: DerogationStatus.APPROVED,
      derogationType: DerogationType.LIMIT,
      derogationValue: 25000,
      proposedEffectiveDate: '18/01/2025',
      proposedEndDate: '18/10/2025',
      daysPending: 0,
      actions: 'Voir'
    },
    {
      id: 4,
      createdAt: '12/01/2025',
      requestType: 'Dérogation procédurale',
      clientName: 'GHI Solutions',
      sabClientId: 'SAB001111',
      owner: 'Pierre Moreau',
      responsable: 'Marie Leroy',
      agency: 'Agence Toulouse',
      proposedStatus: 'Rejetée',
      status: DerogationStatus.REJECTED,
      derogationType: DerogationType.PROCEDURE,
      derogationValue: 5000,
      proposedEffectiveDate: '15/01/2025',
      proposedEndDate: '15/03/2025',
      daysPending: 0,
      actions: 'Voir'
    },
    {
      id: 5,
      createdAt: '11/01/2025',
      requestType: 'Dérogation tarifaire',
      clientName: 'JKL Entreprise',
      sabClientId: 'SAB002222',
      owner: 'Anna Aiello',
      responsable: 'Anna Aiello',
      agency: 'Agence Paris',
      proposedStatus: 'Brouillon',
      status: DerogationStatus.DRAFT,
      derogationType: DerogationType.TARIFF,
      derogationValue: 12000,
      proposedEffectiveDate: '30/01/2025',
      proposedEndDate: '30/06/2025',
      daysPending: 1,
      actions: 'Traiter'
    },
    {
      id: 6,
      createdAt: '10/01/2025',
      requestType: 'Autre dérogation',
      clientName: 'MNO Services',
      sabClientId: 'SAB003333',
      owner: 'Luc Martin',
      responsable: 'Sophie Martin',
      agency: 'Agence Nice',
      proposedStatus: 'Implémentée',
      status: DerogationStatus.IMPLEMENTED,
      derogationType: DerogationType.OTHER,
      derogationValue: 7500,
      proposedEffectiveDate: '12/01/2025',
      proposedEndDate: '12/04/2025',
      daysPending: 0,
      actions: 'Voir'
    },
    {
      id: 7,
      createdAt: '09/01/2025',
      requestType: 'Dérogation de commission',
      clientName: 'PQR Industries',
      sabClientId: 'SAB004444',
      owner: 'Sarah Dubois',
      responsable: 'Marc Dupont',
      agency: 'Agence Bordeaux',
      proposedStatus: 'En cours de traitement',
      status: DerogationStatus.PENDING,
      derogationType: DerogationType.COMMISSION,
      derogationValue: 18000,
      proposedEffectiveDate: '15/01/2025',
      proposedEndDate: '15/08/2025',
      daysPending: 5,
      actions: 'Traiter'
    },
    {
      id: 8,
      createdAt: '08/01/2025',
      requestType: 'Dérogation de limite',
      clientName: 'STU Group',
      sabClientId: 'SAB005555',
      owner: 'Thomas Petit',
      responsable: 'Julie Bernard',
      agency: 'Agence Nantes',
      proposedStatus: 'Annulée',
      status: DerogationStatus.CANCELLED,
      derogationType: DerogationType.LIMIT,
      derogationValue: 30000,
      proposedEffectiveDate: '10/01/2025',
      proposedEndDate: '10/07/2025',
      daysPending: 0,
      actions: 'Voir'
    }
  ];

  private currentUserId = 'user123'; // Simuler un utilisateur connecté

  constructor() {}

  /**
   * Simule l'appel API pour récupérer les demandes à traiter
   */
  getDemandsToProcess(userId: string): Observable<ApiResponse<DashboardRequestDTO[]>> {
    const demandsToProcess = this.mockData.filter(
      demand => demand.status === DerogationStatus.PENDING || demand.status === DerogationStatus.DRAFT
    );

    return of({
      success: true,
      data: demandsToProcess,
      message: 'Demandes à traiter récupérées avec succès',
      timestamp: new Date().toISOString()
    }).pipe(delay(300)); // Simuler un délai réseau
  }

  /**
   * Simule l'appel API pour récupérer les demandes en attente de validation
   */
  getDemandsPendingValidation(userId: string): Observable<ApiResponse<DashboardRequestDTO[]>> {
    const demandsPending = this.mockData.filter(
      demand => demand.status === DerogationStatus.IN_REVIEW
    );

    return of({
      success: true,
      data: demandsPending,
      message: 'Demandes en attente récupérées avec succès',
      timestamp: new Date().toISOString()
    }).pipe(delay(400));
  }

  /**
   * Simule l'appel API pour récupérer les demandes traitées
   */
  getDemandsProcessed(userId: string): Observable<ApiResponse<DashboardRequestDTO[]>> {
    const demandsProcessed = this.mockData.filter(
      demand => [
        DerogationStatus.APPROVED, 
        DerogationStatus.REJECTED, 
        DerogationStatus.IMPLEMENTED, 
        DerogationStatus.CANCELLED
      ].includes(demand.status)
    );

    return of({
      success: true,
      data: demandsProcessed,
      message: 'Demandes traitées récupérées avec succès',
      timestamp: new Date().toISOString()
    }).pipe(delay(350));
  }

  /**
   * Simule l'appel API pour récupérer une demande par ID
   */
  getDemandById(demandId: string): Observable<ApiResponse<DashboardRequestDTO>> {
    const demand = this.mockData.find(d => d.id.toString() === demandId);
    
    if (!demand) {
      return throwError(() => new Error(`Demande avec l'ID ${demandId} non trouvée`)).pipe(delay(200));
    }

    return of({
      success: true,
      data: demand,
      message: 'Demande récupérée avec succès',
      timestamp: new Date().toISOString()
    }).pipe(delay(250));
  }

  /**
   * Simule la mise à jour du statut d'une demande
   */
  updateDemandStatus(demandId: string, newStatus: string, comments?: string): Observable<ApiResponse<any>> {
    const demandIndex = this.mockData.findIndex(d => d.id.toString() === demandId);
    
    if (demandIndex === -1) {
      return throwError(() => new Error(`Demande avec l'ID ${demandId} non trouvée`)).pipe(delay(200));
    }

    // Simuler la mise à jour
    const statusMapping: { [key: string]: DerogationStatus } = {
      'APPROVED': DerogationStatus.APPROVED,
      'REJECTED': DerogationStatus.REJECTED,
      'IN_REVIEW': DerogationStatus.IN_REVIEW,
      'IMPLEMENTED': DerogationStatus.IMPLEMENTED,
      'CANCELLED': DerogationStatus.CANCELLED
    };

    if (statusMapping[newStatus]) {
      this.mockData[demandIndex].status = statusMapping[newStatus];
      this.mockData[demandIndex].proposedStatus = this.getStatusLabel(statusMapping[newStatus]);
      
      // Reset daysPending for final statuses
      if ([DerogationStatus.APPROVED, DerogationStatus.REJECTED, DerogationStatus.IMPLEMENTED, DerogationStatus.CANCELLED].includes(statusMapping[newStatus])) {
        this.mockData[demandIndex].daysPending = 0;
      }
    }

    return of({
      success: true,
      data: { id: demandId, status: newStatus, comments },
      message: `Statut mis à jour vers ${newStatus} avec succès`,
      timestamp: new Date().toISOString()
    }).pipe(delay(400));
  }

  /**
   * Simule la recherche dans les demandes
   */
  searchDemands(userId: string, searchTerm: string, filters?: any): Observable<ApiResponse<DashboardRequestDTO[]>> {
    let filteredData = this.mockData;

    if (searchTerm && searchTerm.trim()) {
      const term = searchTerm.toLowerCase();
      filteredData = this.mockData.filter(demand => 
        demand.clientName.toLowerCase().includes(term) ||
        demand.sabClientId.toLowerCase().includes(term) ||
        demand.owner.toLowerCase().includes(term) ||
        demand.requestType.toLowerCase().includes(term) ||
        demand.agency.toLowerCase().includes(term)
      );
    }

    // Appliquer les filtres additionnels si fournis
    if (filters) {
      if (filters.status) {
        filteredData = filteredData.filter(d => d.status === filters.status);
      }
      if (filters.derogationType) {
        filteredData = filteredData.filter(d => d.derogationType === filters.derogationType);
      }
      if (filters.agency) {
        filteredData = filteredData.filter(d => d.agency === filters.agency);
      }
    }

    return of({
      success: true,
      data: filteredData,
      message: `${filteredData.length} résultat(s) trouvé(s)`,
      timestamp: new Date().toISOString()
    }).pipe(delay(300));
  }

  /**
   * Simule la récupération des statistiques du dashboard
   */
  getDashboardStats(userId: string): Observable<ApiResponse<any>> {
    const stats = {
      totalRequests: this.mockData.length,
      pendingRequests: this.mockData.filter(d => d.status === DerogationStatus.PENDING).length,
      inReviewRequests: this.mockData.filter(d => d.status === DerogationStatus.IN_REVIEW).length,
      approvedRequests: this.mockData.filter(d => d.status === DerogationStatus.APPROVED).length,
      rejectedRequests: this.mockData.filter(d => d.status === DerogationStatus.REJECTED).length,
      draftRequests: this.mockData.filter(d => d.status === DerogationStatus.DRAFT).length,
      averageProcessingTime: 3.5,
      totalValue: this.mockData.reduce((sum, d) => sum + d.derogationValue, 0)
    };

    return of({
      success: true,
      data: stats,
      message: 'Statistiques récupérées avec succès',
      timestamp: new Date().toISOString()
    }).pipe(delay(200));
  }

  /**
   * Simule la création d'une nouvelle demande
   */
  createDemand(demandData: any): Observable<ApiResponse<DashboardRequestDTO>> {
    const newId = Math.max(...this.mockData.map(d => d.id)) + 1;
    
    const newDemand: DashboardRequestDTO = {
      id: newId,
      createdAt: new Date().toLocaleDateString('fr-FR'),
      requestType: demandData.requestType || 'Nouvelle demande',
      clientName: demandData.clientName || 'Client inconnu',
      sabClientId: demandData.sabClientId || 'SAB000000',
      owner: demandData.owner || 'Utilisateur actuel',
      responsable: demandData.responsable || 'Utilisateur actuel',
      agency: demandData.agency || 'Agence inconnue',
      proposedStatus: 'Brouillon',
      status: DerogationStatus.DRAFT,
      derogationType: demandData.derogationType || DerogationType.TARIFF,
      derogationValue: demandData.derogationValue || 0,
      proposedEffectiveDate: demandData.proposedEffectiveDate || new Date().toLocaleDateString('fr-FR'),
      proposedEndDate: demandData.proposedEndDate || new Date().toLocaleDateString('fr-FR'),
      daysPending: 0,
      actions: 'Traiter'
    };

    this.mockData.push(newDemand);

    return of({
      success: true,
      data: newDemand,
      message: 'Demande créée avec succès',
      timestamp: new Date().toISOString()
    }).pipe(delay(500));
  }

  /**
   * Simule la suppression d'une demande
   */
  deleteDemand(demandId: string): Observable<ApiResponse<any>> {
    const demandIndex = this.mockData.findIndex(d => d.id.toString() === demandId);
    
    if (demandIndex === -1) {
      return throwError(() => new Error(`Demande avec l'ID ${demandId} non trouvée`)).pipe(delay(200));
    }

    this.mockData.splice(demandIndex, 1);

    return of({
      success: true,
      data: { id: demandId },
      message: 'Demande supprimée avec succès',
      timestamp: new Date().toISOString()
    }).pipe(delay(300));
  }

  /**
   * Simule l'obtention des demandes avec pagination
   */
  getDemandsWithPagination(
    userId: string, 
    page: number = 0, 
    size: number = 10, 
    sortBy?: string, 
    sortDirection?: 'asc' | 'desc'
  ): Observable<ApiResponse<PageResponse<DashboardRequestDTO>>> {
    
    let sortedData = [...this.mockData];
    
    // Appliquer le tri si spécifié
    if (sortBy) {
      sortedData.sort((a, b) => {
        const aVal = (a as any)[sortBy];
        const bVal = (b as any)[sortBy];
        
        let comparison = 0;
        if (aVal > bVal) comparison = 1;
        if (aVal < bVal) comparison = -1;
        
        return sortDirection === 'desc' ? -comparison : comparison;
      });
    }

    // Calculer la pagination
    const startIndex = page * size;
    const endIndex = startIndex + size;
    const content = sortedData.slice(startIndex, endIndex);
    
    const pageResponse: PageResponse<DashboardRequestDTO> = {
      content,
      totalElements: this.mockData.length,
      totalPages: Math.ceil(this.mockData.length / size),
      size,
      number: page,
      numberOfElements: content.length,
      first: page === 0,
      last: endIndex >= this.mockData.length,
      empty: content.length === 0
    };

    return of({
      success: true,
      data: pageResponse,
      message: 'Demandes paginées récupérées avec succès',
      timestamp: new Date().toISOString()
    }).pipe(delay(350));
  }

  /**
   * Simule des erreurs occasionnelles pour tester la gestion d'erreur
   */
  simulateError(): Observable<never> {
    return throwError(() => new Error('Erreur simulée du serveur mock')).pipe(delay(500));
  }

  /**
   * Utilitaires privées
   */
  private getStatusLabel(status: DerogationStatus): string {
    const labels: { [key in DerogationStatus]: string } = {
      [DerogationStatus.DRAFT]: 'Brouillon',
      [DerogationStatus.PENDING]: 'En attente',
      [DerogationStatus.IN_REVIEW]: 'En cours de traitement',
      [DerogationStatus.APPROVED]: 'Approuvée',
      [DerogationStatus.REJECTED]: 'Rejetée',
      [DerogationStatus.IMPLEMENTED]: 'Implémentée',
      [DerogationStatus.CANCELLED]: 'Annulée'
    };
    return labels[status];
  }

  /**
   * Méthode pour réinitialiser les données mock (utile pour les tests)
   */
  resetMockData(): void {
    // On pourrait recharger les données depuis un fichier JSON ou les réinitialiser
    console.log('Données mock réinitialisées');
  }

  /**
   * Méthode pour ajouter des données mock supplémentaires
   */
  addMockData(data: DashboardRequestDTO[]): void {
    this.mockData.push(...data);
  }
}
