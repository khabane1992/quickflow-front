// src/app/core/services/example-usage.service.ts
// FICHIER D'EXEMPLE - Montre comment utiliser le mock server dans vos services

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { firstValueFrom, lastValueFrom } from 'rxjs';
import { ApiConfigService } from './api-config.service';
import { NotificationService } from './notification.service';

/**
 * Service d'exemple montrant les bonnes pratiques avec le mock server
 */
@Injectable({
  providedIn: 'root'
})
export class ExampleUsageService {

  private baseUrl: string;

  constructor(
    private http: HttpClient,
    private apiConfig: ApiConfigService,
    private notificationService: NotificationService
  ) {
    this.baseUrl = this.apiConfig.buildApiUrl('example');
  }

  /**
   * EXEMPLE 1: Service basique avec logging
   */
  getExampleData(): Observable<any[]> {
    this.apiConfig.logMock('ExampleService: Récupération des données d\'exemple');

    return this.http.get<any[]>(`${this.baseUrl}/data`);
  }

  /**
   * EXEMPLE 2: Service avec gestion d'erreur et fallback
   */
  getDataWithFallback(): Observable<any[]> {
    return new Observable(observer => {
      this.http.get<any[]>(`${this.baseUrl}/data`).subscribe({
        next: (data) => {
          this.apiConfig.logMock('Données récupérées avec succès', { count: data.length });
          observer.next(data);
          observer.complete();
        },
        error: (error) => {
          this.apiConfig.logMock('Erreur lors de la récupération', { error });

          if (!this.apiConfig.isMockEnabled()) {
            // Si on est en mode real et qu'il y a une erreur, proposer le mock
            this.notificationService.showBackendUnavailable(() => {
              this.apiConfig.enableMockApi();
              // Relancer la requête
              this.getDataWithFallback().subscribe(observer);
            });
          } else {
            observer.error(error);
          }
        }
      });
    });
  }

  /**
   * EXEMPLE 3: Service avec vérification de connectivité
   */
  async getDataWithConnectivityCheck(): Promise<any[]> {
    // Vérifier la connectivité seulement si on n'est pas en mode mock
    if (!this.apiConfig.isMockEnabled()) {
      const isConnected = await this.apiConfig.checkBackendConnectivity();

      if (!isConnected) {
        this.notificationService.showBackendUnavailable(() => {
          this.apiConfig.enableMockApi();
        });
        throw new Error('Backend indisponible');
      }
    }

    return await firstValueFrom(this.http.get<any[]>(`${this.baseUrl}/data`));
  }

  /**
   * EXEMPLE 4: Service avec basculement automatique
   */
  async getDataWithAutoSwitch(): Promise<any[]> {
    try {
      // Tenter un basculement automatique si nécessaire
      await this.apiConfig.autoSwitchToMockIfNeeded();

      return await firstValueFrom(this.http.get<any[]>(`${this.baseUrl}/data`));
    } catch (error) {
      this.notificationService.showError(
        'Erreur de récupération',
        'Impossible de récupérer les données'
      );
      throw error;
    }
  }

  /**
   * EXEMPLE 5: Service avec notification de mode
   */
  getDataWithModeNotification(): Observable<any[]> {
    // Informer l'utilisateur du mode utilisé
    const mode = this.apiConfig.isMockEnabled() ? 'simulation' : 'réel';
    this.notificationService.showInfo(
      'Chargement des données',
      `Récupération depuis le serveur ${mode}`
    );

    return this.http.get<any[]>(`${this.baseUrl}/data`);
  }

  /**
   * EXEMPLE 6: Méthode pour développeurs - basculement manuel
   */
  switchToMockForTesting(): void {
    if (!this.apiConfig.isMockEnabled()) {
      this.apiConfig.enableMockApi();
      this.notificationService.showInfo(
        'Mode développement',
        'Basculement vers le mock pour les tests'
      );
    }
  }

  switchToRealForTesting(): void {
    if (this.apiConfig.isMockEnabled()) {
      this.apiConfig.disableMockApi();
      this.notificationService.showInfo(
        'Mode production',
        'Basculement vers l\'API réelle pour les tests'
      );
    }
  }

  /**
   * EXEMPLE 7: Diagnostic et debugging
   */
  async diagnoseApiConnection(): Promise<void> {
    const config = this.apiConfig.getConfigInfo();

    console.group('🔍 Diagnostic API Connection');
    console.log('Mode actuel:', config.useMockApi ? 'MOCK' : 'REAL');
    console.log('URL API:', config.apiUrl);
    console.log('Environment:', config.production ? 'PRODUCTION' : 'DEVELOPMENT');

    if (!config.useMockApi) {
      console.log('Test de connectivité...');
      const isConnected = await this.apiConfig.checkBackendConnectivity();
      console.log('Backend accessible:', isConnected ? '✅' : '❌');

      if (!isConnected) {
        console.warn('💡 Suggestion: Basculer vers le mock avec apiConfig.enableMockApi()');
      }
    } else {
      console.log('Mode mock actif - pas de test de connectivité nécessaire');
    }

    console.groupEnd();
  }

  /**
   * EXEMPLE 8: Utilitaires pour les tests E2E
   */
  enableMockForE2E(): void {
    this.apiConfig.enableMockApi();
    // Désactiver les notifications pour les tests
    (this.apiConfig as any).enableMockLogs = false;
  }

  resetMockDataForTesting(): void {
    // Cette méthode devrait être ajoutée à MockApiService
    console.log('Reset des données mock pour les tests');
  }

  /**
   * EXEMPLE 9: Gestion des environnements multiples
   */
  getApiMode(): 'mock' | 'real' | 'hybrid' {
    if (this.apiConfig.isMockEnabled()) {
      return 'mock';
    } else {
      // On pourrait avoir un mode hybride où certains services sont mockés
      return 'real';
    }
  }

  /**
   * EXEMPLE 10: Performance monitoring
   */
  async getDataWithPerformanceMonitoring(): Promise<any[]> {
    const startTime = performance.now();
    const mode = this.getApiMode();

    try {
      const data = await firstValueFrom(this.http.get<any[]>(`${this.baseUrl}/data`));
      const endTime = performance.now();
      const duration = endTime - startTime;

      this.apiConfig.logMock(`Performance: ${mode} mode took ${duration.toFixed(2)}ms`);

      // Log des performances anormalement lentes
      if (duration > 2000) {
        this.notificationService.showWarning(
          'Performance',
          `Requête lente détectée: ${duration.toFixed(0)}ms`
        );
      }

      return data;
    } catch (error) {
      const endTime = performance.now();
      const duration = endTime - startTime;
      this.apiConfig.logMock(`Error after ${duration.toFixed(2)}ms in ${mode} mode`, error);
      throw error;
    }
  }
}

/**
 * EXEMPLE D'UTILISATION DANS UN COMPOSANT:
 *
 * constructor(
 *   private exampleService: ExampleUsageService,
 *   private apiConfig: ApiConfigService
 * ) {}
 *
 * async ngOnInit() {
 *   // Diagnostic au démarrage
 *   await this.exampleService.diagnoseApiConnection();
 *
 *   // Chargement des données avec gestion d'erreur
 *   try {
 *     const data = await this.exampleService.getDataWithAutoSwitch();
 *     console.log('Données chargées:', data);
 *   } catch (error) {
 *     console.error('Erreur de chargement:', error);
 *   }
 * }
 *
 * // Bouton pour basculer le mode (développement uniquement)
 * toggleApiMode(): void {
 *   this.apiConfig.toggleMockApi();
 * }
 */
