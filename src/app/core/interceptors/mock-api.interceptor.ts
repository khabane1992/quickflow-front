// src/app/core/interceptors/mock-api.interceptor.ts

import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { delay, switchMap } from 'rxjs/operators';
import { MockApiService } from '../mock/mock-api.service';
import { environment } from '../../../environments/environment';

@Injectable()
export class MockApiInterceptor implements HttpInterceptor {

  constructor(private mockApiService: MockApiService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    
    // Si le mock n'est pas activé, continuer normalement
    if (!environment.useMockApi) {
      return next.handle(req);
    }

    // Analyser l'URL pour déterminer quel endpoint mock appeler
    const url = req.url;
    const method = req.method;

    // Dashboard endpoints
    if (url.includes('/api/dashboard') || url.includes('/api/demands')) {
      return this.handleDashboardEndpoints(req, url, method);
    }

    // Si aucun endpoint mock correspondant, passer au handler suivant
    return next.handle(req);
  }

  private handleDashboardEndpoints(req: HttpRequest<any>, url: string, method: string): Observable<HttpEvent<any>> {
    const params = this.extractParams(req);
    const userId = params.get('userId') || 'mock-user';

    // GET /api/dashboard/demands-to-process
    if (method === 'GET' && url.includes('demands-to-process')) {
      return this.mockApiService.getDemandsToProcess(userId).pipe(
        switchMap(response => of(new HttpResponse({
          status: 200,
          body: response
        })))
      );
    }

    // GET /api/dashboard/demands-pending-validation
    if (method === 'GET' && url.includes('demands-pending-validation')) {
      return this.mockApiService.getDemandsPendingValidation(userId).pipe(
        switchMap(response => of(new HttpResponse({
          status: 200,
          body: response
        })))
      );
    }

    // GET /api/dashboard/demands-processed
    if (method === 'GET' && url.includes('demands-processed')) {
      // Vérifier si c'est une demande paginée
      const page = params.get('page');
      const size = params.get('size');
      
      if (page !== null && size !== null) {
        return this.mockApiService.getDemandsWithPagination(
          userId, 
          parseInt(page), 
          parseInt(size),
          params.get('sortBy') || undefined,
          params.get('sortDirection') as 'asc' | 'desc' || undefined
        ).pipe(
          switchMap(response => of(new HttpResponse({
            status: 200,
            body: response
          })))
        );
      } else {
        return this.mockApiService.getDemandsProcessed(userId).pipe(
          switchMap(response => of(new HttpResponse({
            status: 200,
            body: response
          })))
        );
      }
    }

    // GET /api/dashboard/demands/{id}
    if (method === 'GET' && url.includes('/demands/') && !url.includes('search') && !url.includes('status')) {
      const demandId = this.extractIdFromUrl(url, 'demands');
      return this.mockApiService.getDemandById(demandId).pipe(
        switchMap(response => of(new HttpResponse({
          status: 200,
          body: response
        })))
      );
    }

    // PUT /api/dashboard/demands/{id}/status
    if (method === 'PUT' && url.includes('/status')) {
      const demandId = this.extractIdFromUrl(url, 'demands');
      const body = req.body;
      
      return this.mockApiService.updateDemandStatus(
        demandId, 
        body?.status, 
        body?.comments
      ).pipe(
        switchMap(response => of(new HttpResponse({
          status: 200,
          body: response
        })))
      );
    }

    // GET /api/dashboard/demands-by-status
    if (method === 'GET' && url.includes('demands-by-status')) {
      const status = params.get('status');
      // Simuler la recherche par statut
      return this.mockApiService.searchDemands(userId, '', { status }).pipe(
        switchMap(response => of(new HttpResponse({
          status: 200,
          body: response
        })))
      );
    }

    // GET /api/dashboard/demands/search
    if (method === 'GET' && url.includes('/search')) {
      const searchTerm = params.get('search') || '';
      const filters = this.extractFilters(params);
      
      return this.mockApiService.searchDemands(userId, searchTerm, filters).pipe(
        switchMap(response => of(new HttpResponse({
          status: 200,
          body: response
        })))
      );
    }

    // GET /api/dashboard/stats
    if (method === 'GET' && url.includes('/stats')) {
      return this.mockApiService.getDashboardStats(userId).pipe(
        switchMap(response => of(new HttpResponse({
          status: 200,
          body: response
        })))
      );
    }

    // POST /api/demands (création d'une nouvelle demande)
    if (method === 'POST' && (url.includes('/api/demands') || url.includes('demands'))) {
      return this.mockApiService.createDemand(req.body).pipe(
        switchMap(response => of(new HttpResponse({
          status: 201,
          body: response
        })))
      );
    }

    // DELETE /api/demands/{id}
    if (method === 'DELETE' && url.includes('/demands/')) {
      const demandId = this.extractIdFromUrl(url, 'demands');
      return this.mockApiService.deleteDemand(demandId).pipe(
        switchMap(response => of(new HttpResponse({
          status: 200,
          body: response
        })))
      );
    }

    // Endpoint générique pour les demandes (GET /api/demands)
    if (method === 'GET' && url.includes('/api/demands')) {
      const type = params.get('type');
      
      switch (type) {
        case 'TO_PROCESS':
          return this.mockApiService.getDemandsToProcess(userId).pipe(
            switchMap(response => of(new HttpResponse({
              status: 200,
              body: response.data // RequestService attend directement les données
            })))
          );
          
        case 'PENDING_VALIDATION':
          return this.mockApiService.getDemandsPendingValidation(userId).pipe(
            switchMap(response => of(new HttpResponse({
              status: 200,
              body: response.data
            })))
          );
          
        case 'PROCESSED':
          return this.mockApiService.getDemandsProcessed(userId).pipe(
            switchMap(response => of(new HttpResponse({
              status: 200,
              body: response.data
            })))
          );
          
        default:
          // Retourner toutes les demandes mockées
          return this.mockApiService.searchDemands(userId, '').pipe(
            switchMap(response => of(new HttpResponse({
              status: 200,
              body: response.data
            })))
          );
      }
    }

    // Si aucun endpoint correspondant n'est trouvé, retourner une erreur 404
    return of(new HttpResponse({
      status: 404,
      body: {
        success: false,
        message: `Mock endpoint not found for ${method} ${url}`,
        timestamp: new Date().toISOString()
      }
    }));
  }

  private extractParams(req: HttpRequest<any>): URLSearchParams {
    const url = new URL(req.url, 'http://localhost');
    return url.searchParams;
  }

  private extractIdFromUrl(url: string, resource: string): string {
    const regex = new RegExp(`/${resource}/([^/]+)`);
    const match = url.match(regex);
    return match ? match[1] : '';
  }

  private extractFilters(params: URLSearchParams): any {
    const filters: any = {};
    
    // Liste des paramètres de filtre connus
    const filterParams = [
      'status', 'derogationType', 'agency', 'owner', 
      'dateFrom', 'dateTo', 'minValue', 'maxValue'
    ];

    filterParams.forEach(param => {
      const value = params.get(param);
      if (value) {
        filters[param] = value;
      }
    });

    return Object.keys(filters).length > 0 ? filters : undefined;
  }
}
