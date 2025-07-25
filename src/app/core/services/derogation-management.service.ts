// src/app/core/services/dashboard.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map, retry } from 'rxjs/operators';
import {
  ApiResponse, DashboardMapper,
  DashboardRequestDTO,
  DerogationRequest, PageResponse
} from "../../shared/interfaces/derogation-mangement.interface";
import { ApiConfigService } from './api-config.service';


@Injectable({
  providedIn: 'root'
})
export class DarogationManagementService {

  private readonly baseUrl: string;

  constructor(
    private http: HttpClient,
    private apiConfig: ApiConfigService
  ) {
    this.baseUrl = this.apiConfig.buildApiUrl('dashboard');
  }

  /**
   * Récupère les demandes à traiter
   */
  getDemandsToProcess(userId: string): Observable<DerogationRequest[]> {
    const params = new HttpParams().set('userId', userId);

    this.apiConfig.logMock('Récupération des demandes à traiter', { userId });

    return this.http.get<ApiResponse<DashboardRequestDTO[]>>(`${this.baseUrl}/demands-to-process`, { params })
      .pipe(
        retry(2), // Retry 2 fois en cas d'erreur réseau
        map(response => {
          if (response.success && response.data) {
            const mappedData = response.data.map(dto => DashboardMapper.mapToDerogationRequest(dto));
            this.apiConfig.logMock('Demandes à traiter récupérées', { count: mappedData.length });
            return mappedData;
          }
          throw new Error(response.message || 'Erreur lors de la récupération des demandes à traiter');
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Récupère les demandes en attente de validation
   */
  getDemandsPendingValidation(userId: string): Observable<DerogationRequest[]> {
    const params = new HttpParams().set('userId', userId);

    this.apiConfig.logMock('Récupération des demandes en attente', { userId });

    return this.http.get<ApiResponse<DashboardRequestDTO[]>>(`${this.baseUrl}/demands-pending-validation`, { params })
      .pipe(
        retry(2),
        map(response => {
          if (response.success && response.data) {
            const mappedData = response.data.map(dto => DashboardMapper.mapToDerogationRequest(dto));
            this.apiConfig.logMock('Demandes en attente récupérées', { count: mappedData.length });
            return mappedData;
          }
          throw new Error(response.message || 'Erreur lors de la récupération des demandes en attente');
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Récupère les demandes traitées
   */
  getDemandsProcessed(userId: string): Observable<DerogationRequest[]> {
    const params = new HttpParams().set('userId', userId);

    this.apiConfig.logMock('Récupération des demandes traitées', { userId });

    return this.http.get<ApiResponse<DashboardRequestDTO[]>>(`${this.baseUrl}/demands-processed`, { params })
      .pipe(
        retry(2),
        map(response => {
          if (response.success && response.data) {
            const mappedData = response.data.map(dto => DashboardMapper.mapToDerogationRequest(dto));
            this.apiConfig.logMock('Demandes traitées récupérées', { count: mappedData.length });
            return mappedData;
          }
          throw new Error(response.message || 'Erreur lors de la récupération des demandes traitées');
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Récupère les demandes traitées avec pagination (si nécessaire)
   */
  getDemandsProcessedPaginated(userId: string, page: number = 0, size: number = 20): Observable<PageResponse<DerogationRequest>> {
    const params = new HttpParams()
      .set('userId', userId)
      .set('page', page.toString())
      .set('size', size.toString());

    this.apiConfig.logMock('Récupération des demandes traitées paginées', { userId, page, size });

    return this.http.get<ApiResponse<PageResponse<DashboardRequestDTO>>>(`${this.baseUrl}/demands-processed`, { params })
      .pipe(
        retry(2),
        map(response => {
          if (response.success && response.data) {
            const pageData = response.data;
            const mappedContent = pageData.content.map(dto => DashboardMapper.mapToDerogationRequest(dto));
            this.apiConfig.logMock('Demandes traitées paginées récupérées', {
              page: pageData.number,
              totalElements: pageData.totalElements
            });
            return {
              ...pageData,
              content: mappedContent
            };
          }
          throw new Error(response.message || 'Erreur lors de la récupération des demandes traitées');
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Récupère toutes les demandes par statut
   */
  getAllDemandsByStatus(userId: string, status: string): Observable<DerogationRequest[]> {
    const params = new HttpParams()
      .set('userId', userId)
      .set('status', status);

    this.apiConfig.logMock('Récupération des demandes par statut', { userId, status });

    return this.http.get<ApiResponse<DashboardRequestDTO[]>>(`${this.baseUrl}/demands-by-status`, { params })
      .pipe(
        retry(2),
        map(response => {
          if (response.success && response.data) {
            const mappedData = response.data.map(dto => DashboardMapper.mapToDerogationRequest(dto));
            this.apiConfig.logMock('Demandes par statut récupérées', { status, count: mappedData.length });
            return mappedData;
          }
          throw new Error(response.message || 'Erreur lors de la récupération des demandes');
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Récupère une demande spécifique par ID
   */
  getDemandById(demandId: string): Observable<DashboardRequestDTO> {
    this.apiConfig.logMock('Récupération de la demande par ID', { demandId });

    return this.http.get<ApiResponse<DashboardRequestDTO>>(`${this.baseUrl}/demands/${demandId}`)
      .pipe(
        retry(2),
        map(response => {
          if (response.success && response.data) {
            this.apiConfig.logMock('Demande récupérée par ID', { id: response.data.id });
            return response.data;
          }
          throw new Error(response.message || 'Erreur lors de la récupération de la demande');
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Met à jour le statut d'une demande
   */
  updateDemandStatus(demandId: string, newStatus: string, comments?: string): Observable<boolean> {
    const body = {
      status: newStatus,
      comments: comments
    };

    this.apiConfig.logMock('Mise à jour du statut de la demande', { demandId, newStatus, comments });

    return this.http.put<ApiResponse<any>>(`${this.baseUrl}/demands/${demandId}/status`, body)
      .pipe(
        map(response => {
          this.apiConfig.logMock('Statut mis à jour', { demandId, success: response.success });
          return response.success;
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Approuve une demande
   */
  approveDemand(demandId: string, comments?: string): Observable<boolean> {
    this.apiConfig.logMock('Approbation de la demande', { demandId, comments });
    return this.updateDemandStatus(demandId, 'APPROVED', comments);
  }

  /**
   * Rejette une demande
   */
  rejectDemand(demandId: string, comments?: string): Observable<boolean> {
    this.apiConfig.logMock('Rejet de la demande', { demandId, comments });
    return this.updateDemandStatus(demandId, 'REJECTED', comments);
  }

  /**
   * Traite une demande (passage au statut en cours de traitement)
   */
  processDemand(demandId: string): Observable<boolean> {
    this.apiConfig.logMock('Traitement de la demande', { demandId });
    return this.updateDemandStatus(demandId, 'IN_REVIEW');
  }

  /**
   * Récupère les statistiques du dashboard
   */
  getDashboardStats(userId: string): Observable<any> {
    const params = new HttpParams().set('userId', userId);

    this.apiConfig.logMock('Récupération des statistiques', { userId });

    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/stats`, { params })
      .pipe(
        retry(2),
        map(response => {
          if (response.success && response.data) {
            this.apiConfig.logMock('Statistiques récupérées', response.data);
            return response.data;
          }
          throw new Error(response.message || 'Erreur lors de la récupération des statistiques');
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Recherche dans les demandes
   */
  searchDemands(userId: string, searchTerm: string, filters?: any): Observable<DerogationRequest[]> {
    let params = new HttpParams()
      .set('userId', userId)
      .set('search', searchTerm);

    if (filters) {
      Object.keys(filters).forEach(key => {
        if (filters[key] !== null && filters[key] !== undefined) {
          params = params.set(key, filters[key]);
        }
      });
    }

    this.apiConfig.logMock('Recherche de demandes', { userId, searchTerm, filters });

    return this.http.get<ApiResponse<DashboardRequestDTO[]>>(`${this.baseUrl}/demands/search`, { params })
      .pipe(
        retry(2),
        map(response => {
          if (response.success && response.data) {
            const mappedData = response.data.map(dto => DashboardMapper.mapToDerogationRequest(dto));
            this.apiConfig.logMock('Résultats de recherche', { count: mappedData.length });
            return mappedData;
          }
          throw new Error(response.message || 'Erreur lors de la recherche');
        }),
        catchError(this.handleError.bind(this))
      );
  }

  /**
   * Gestion centralisée des erreurs
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Une erreur inconnue est survenue';

    if (error.error instanceof ErrorEvent) {
      // Erreur côté client
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      // Erreur côté serveur
      switch (error.status) {
        case 400:
          errorMessage = 'Requête invalide. Vérifiez les données envoyées.';
          break;
        case 401:
          errorMessage = 'Non autorisé. Veuillez vous reconnecter.';
          break;
        case 403:
          errorMessage = 'Accès refusé. Vous n\'avez pas les permissions nécessaires.';
          break;
        case 404:
          errorMessage = 'Ressource non trouvée.';
          break;
        case 500:
          errorMessage = 'Erreur interne du serveur. Veuillez réessayer plus tard.';
          break;
        default:
          errorMessage = `Erreur ${error.status}: ${error.message}`;
      }

      if (error.error?.message) {
        errorMessage = error.error.message;
      }
    }

    const apiMode = this.apiConfig.isMockEnabled() ? 'MOCK' : 'REAL';
    console.error(`Erreur API Dashboard (${apiMode}):`, errorMessage, error);
    this.apiConfig.logMock('Erreur API', { error: errorMessage, mode: apiMode });

    return throwError(() => new Error(errorMessage));
  }

  /**
   * Méthodes utilitaires pour le debugging
   */
  getApiMode(): string {
    return this.apiConfig.isMockEnabled() ? 'MOCK' : 'REAL';
  }

  getApiBaseUrl(): string {
    return this.baseUrl;
  }

  toggleApiMode(): void {
    this.apiConfig.toggleMockApi();
  }
}
