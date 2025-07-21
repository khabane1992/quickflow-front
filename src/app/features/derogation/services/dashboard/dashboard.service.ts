import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { DashboardRequestDTO, DerogationStatus, DerogationType, DemandsType } from '../../models/dashboard/request.interface';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private apiUrl = '/api';

  constructor(private http: HttpClient) {}

  // Méthode principale qui appelle votre endpoint
  getDemands(userId: string, type: DemandsType): Observable<DashboardRequestDTO[]> {
    let params = new HttpParams()
      .set('userId', userId)
      .set('type', type);

    return this.http.get<DashboardRequestDTO[]>(`${this.apiUrl}/demands`, { params });
  }

  // Méthodes de convenance
  getDemandsToProcess(userId: string): Observable<DashboardRequestDTO[]> {
    return this.getDemands(userId, DemandsType.TO_PROCESS);
  }

  getDemandsPendingValidation(userId: string): Observable<DashboardRequestDTO[]> {
    return this.getDemands(userId, DemandsType.PENDING_VALIDATION);
  }

  getDemandsProcessed(userId: string): Observable<DashboardRequestDTO[]> {
    return this.getDemands(userId, DemandsType.PROCESSED);
  }

  // Données mockées pour les tests
  getMockData(): Observable<DashboardRequestDTO[]> {
    const mockData: DashboardRequestDTO[] = [
      {
        id: 1,
        createdAt: '15/01/2025',
        requestType: 'Navigation logiciels',
        clientName: 'Client Test 1',
        sabClientId: 'SAB001',
        owner: 'Anna Aiello',
        responsable: 'Anna Aiello',
        agency: 'Agence Paris',
        proposedStatus: 'En cours',
        status: DerogationStatus.TO_PROCESS,
        derogationType: DerogationType.NAVIGATION_LOGICIELS,
        derogationValue: 1000,
        proposedEffectiveDate: '20/01/2025',
        proposedEndDate: '20/02/2025',
        daysPending: 3,
        actions: 'Traiter'
      },
      {
        id: 2,
        createdAt: '14/01/2025',
        requestType: 'Droit de diffusion',
        clientName: 'Client Test 2',
        sabClientId: 'SAB002',
        owner: 'Anna Aiello',
        responsable: 'Anna Aiello',
        agency: 'Agence Lyon',
        proposedStatus: 'En attente',
        status: DerogationStatus.PENDING_VALIDATION,
        derogationType: DerogationType.DROIT_DIFFUSION,
        derogationValue: 1500,
        proposedEffectiveDate: '25/01/2025',
        proposedEndDate: '25/03/2025',
        daysPending: 2,
        actions: 'Valider'
      },
      {
        id: 3,
        createdAt: '13/01/2025',
        requestType: 'Données tiers',
        clientName: 'Client Test 3',
        sabClientId: 'SAB003',
        owner: 'Anna Aiello',
        responsable: 'Anna Aiello',
        agency: 'Agence Marseille',
        proposedStatus: 'Traité',
        status: DerogationStatus.PROCESSED,
        derogationType: DerogationType.DONNEES_TIERS,
        derogationValue: 2000,
        proposedEffectiveDate: '18/01/2025',
        proposedEndDate: '18/04/2025',
        daysPending: 0,
        actions: 'Voir'
      }
    ];

    return of(mockData);
  }
}