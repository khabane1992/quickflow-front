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


@Injectable({
  providedIn: 'root'
})
export class DarogationManagementService {

  private readonly baseUrl = '/api/dashboard'; // Ajustez selon votre configuration

  constructor(private http: HttpClient) {}

  /**
   * Récupère les demandes à traiter
   */
  getDemandsToProcess(userId: string): Observable<DerogationRequest[]> {
    const params = new HttpParams().set('userId', userId);

    return this.http.get<ApiResponse<DashboardRequestDTO[]>>(`${this.baseUrl}/demands-to-process`, { params })
      .pipe(
        retry(2), // Retry 2 fois en cas d'erreur réseau
        map(response => {
          if (response.success && response.data) {
            return response.data.map(dto => DashboardMapper.mapToDerogationRequest(dto));
          }
          throw new Error(response.message || 'Erreur lors de la récupération des demandes à traiter');
        }),
        catchError(this.handleError)
      );
  }

  /**
   * Récupère les demandes en attente de validation
   */
  getDemandsPendingValidation(userId: string): Observable<DerogationRequest[]> {
    const params = new HttpParams().set('userId', userId);

    return this.http.get<ApiResponse<DashboardRequestDTO[]>>(`${this.baseUrl}/demands-pending-validation`, { params })
      .pipe(
        retry(2),
        map(response => {
          if (response.success && response.data) {
            return response.data.map(dto => DashboardMapper.mapToDerogationRequest(dto));
          }
          throw new Error(response.message || 'Erreur lors de la récupération des demandes en attente');
        }),
        catchError(this.handleError)
      );
  }

  /**
   * Récupère les demandes traitées
   */
  getDemandsProcessed(userId: string): Observable<DerogationRequest[]> {
    const params = new HttpParams().set('userId', userId);

    return this.http.get<ApiResponse<DashboardRequestDTO[]>>(`${this.baseUrl}/demands-processed`, { params })
      .pipe(
        retry(2),
        map(response => {
          if (response.success && response.data) {
            return response.data.map(dto => DashboardMapper.mapToDerogationRequest(dto));
          }
          throw new Error(response.message || 'Erreur lors de la récupération des demandes traitées');
        }),
        catchError(this.handleError)
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

    return this.http.get<ApiResponse<PageResponse<DashboardRequestDTO>>>(`${this.baseUrl}/demands-processed`, { params })
      .pipe(
        retry(2),
        map(response => {
          if (response.success && response.data) {
            const pageData = response.data;
            return {
              ...pageData,
              content: pageData.content.map(dto => DashboardMapper.mapToDerogationRequest(dto))
            };
          }
          throw new Error(response.message || 'Erreur lors de la récupération des demandes traitées');
        }),
        catchError(this.handleError)
      );
  }

  /**
   * Récupère toutes les demandes par statut
   */
  getAllDemandsByStatus(userId: string, status: string): Observable<DerogationRequest[]> {
    const params = new HttpParams()
      .set('userId', userId)
      .set('status', status);

    return this.http.get<ApiResponse<DashboardRequestDTO[]>>(`${this.baseUrl}/demands-by-status`, { params })
      .pipe(
        retry(2),
        map(response => {
          if (response.success && response.data) {
            return response.data.map(dto => DashboardMapper.mapToDerogationRequest(dto));
          }
          throw new Error(response.message || 'Erreur lors de la récupération des demandes');
        }),
        catchError(this.handleError)
      );
  }

  /**
   * Récupère une demande spécifique par ID
   */
  getDemandById(demandId: string): Observable<DashboardRequestDTO> {
    return this.http.get<ApiResponse<DashboardRequestDTO>>(`${this.baseUrl}/demands/${demandId}`)
      .pipe(
        retry(2),
        map(response => {
          if (response.success && response.data) {
            return response.data;
          }
          throw new Error(response.message || 'Erreur lors de la récupération de la demande');
        }),
        catchError(this.handleError)
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

    return this.http.put<ApiResponse<any>>(`${this.baseUrl}/demands/${demandId}/status`, body)
      .pipe(
        map(response => response.success),
        catchError(this.handleError)
      );
  }

  /**
   * Approuve une demande
   */
  approveDemand(demandId: string, comments?: string): Observable<boolean> {
    return this.updateDemandStatus(demandId, 'APPROVED', comments);
  }

  /**
   * Rejette une demande
   */
  rejectDemand(demandId: string, comments?: string): Observable<boolean> {
    return this.updateDemandStatus(demandId, 'REJECTED', comments);
  }

  /**
   * Traite une demande (passage au statut en cours de traitement)
   */
  processDemand(demandId: string): Observable<boolean> {
    return this.updateDemandStatus(demandId, 'IN_REVIEW');
  }

  /**
   * Récupère les statistiques du dashboard
   */
  getDashboardStats(userId: string): Observable<any> {
    const params = new HttpParams().set('userId', userId);

    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/stats`, { params })
      .pipe(
        retry(2),
        map(response => {
          if (response.success && response.data) {
            return response.data;
          }
          throw new Error(response.message || 'Erreur lors de la récupération des statistiques');
        }),
        catchError(this.handleError)
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

    return this.http.get<ApiResponse<DashboardRequestDTO[]>>(`${this.baseUrl}/demands/search`, { params })
      .pipe(
        retry(2),
        map(response => {
          if (response.success && response.data) {
            return response.data.map(dto => DashboardMapper.mapToDerogationRequest(dto));
          }
          throw new Error(response.message || 'Erreur lors de la recherche');
        }),
        catchError(this.handleError)
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

    console.error('Erreur API Dashboard:', errorMessage, error);
    return throwError(() => new Error(errorMessage));
  }
}
