// src/app/core/services/api-config.service.ts

import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiConfigService {

  constructor() {}

  /**
   * Retourne l'URL de base de l'API
   */
  getApiUrl(): string {
    return environment.apiUrl;
  }

  /**
   * Indique si le mock API est activé
   */
  isMockEnabled(): boolean {
    return environment.useMockApi;
  }

  /**
   * Construit l'URL complète pour un endpoint
   */
  buildApiUrl(endpoint: string): string {
    const baseUrl = this.getApiUrl();
    const cleanEndpoint = endpoint.startsWith('/') ? endpoint.slice(1) : endpoint;
    return `${baseUrl}/${cleanEndpoint}`;
  }

  /**
   * Active ou désactive le mock API à la volée
   * (Utile pour les tests ou le développement)
   */
  toggleMockApi(): void {
    (environment as any).useMockApi = !environment.useMockApi;
    console.log(`Mock API ${environment.useMockApi ? 'activé' : 'désactivé'}`);
  }

  /**
   * Force l'activation du mock API
   */
  enableMockApi(): void {
    (environment as any).useMockApi = true;
    console.log('Mock API activé');
  }

  /**
   * Force la désactivation du mock API
   */
  disableMockApi(): void {
    (environment as any).useMockApi = false;
    console.log('Mock API désactivé');
  }

  /**
   * Retourne la configuration des délais de simulation
   */
  getMockDelays(): { short: number; medium: number; long: number } {
    return environment.mockApiDelays;
  }

  /**
   * Retourne le taux d'erreurs simulées
   */
  getMockErrorRate(): number {
    return environment.mockErrorRate;
  }

  /**
   * Indique si le logging des mocks est activé
   */
  isMockLoggingEnabled(): boolean {
    return environment.enableMockLogs;
  }

  /**
   * Retourne la configuration des fonctionnalités
   */
  getFeatures(): any {
    return environment.features;
  }

  /**
   * Vérifie si une fonctionnalité est activée
   */
  isFeatureEnabled(featureName: string): boolean {
    return environment.features?.[featureName] === true;
  }

  /**
   * Log conditionnel pour le mock API
   */
  logMock(message: string, data?: any): void {
    if (this.isMockLoggingEnabled()) {
      console.log(`[MOCK API] ${message}`, data || '');
    }
  }

  /**
   * Retourne les informations de configuration actuelles
   */
  getConfigInfo(): any {
    return {
      production: environment.production,
      apiUrl: environment.apiUrl,
      useMockApi: environment.useMockApi,
      mockApiDelays: environment.mockApiDelays,
      mockErrorRate: environment.mockErrorRate,
      enableMockLogs: environment.enableMockLogs,
      features: environment.features
    };
  }

  /**
   * Vérifie la connectivité avec le vrai backend
   */
  async checkBackendConnectivity(): Promise<boolean> {
    if (this.isMockEnabled()) {
      return false; // Mock activé, pas de vrai backend
    }

    try {
      const response = await fetch(`${this.getApiUrl()}/health`, {
        method: 'GET',
        timeout: 5000
      } as any);
      return response.ok;
    } catch (error) {
      console.warn('Backend non disponible, basculement vers le mock recommandé');
      return false;
    }
  }

  /**
   * Bascule automatiquement vers le mock si le backend n'est pas disponible
   */
  async autoSwitchToMockIfNeeded(): Promise<void> {
    if (this.isMockEnabled()) {
      return; // Déjà en mode mock
    }

    const isBackendAvailable = await this.checkBackendConnectivity();
    if (!isBackendAvailable) {
      this.enableMockApi();
      console.warn('Backend indisponible, basculement automatique vers le mock API');
    }
  }
}
