// src/app/shared/components/api-debug-panel/api-debug-panel.component.ts

import { Component, OnInit } from '@angular/core';
import { ApiConfigService } from '../../../core/services/api-config.service';
import { NotificationService } from '../../../core/services/notification.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-api-debug-panel',
  templateUrl: './api-debug-panel.component.html',
  styleUrls: ['./api-debug-panel.component.scss']
})
export class ApiDebugPanelComponent implements OnInit {

  isVisible = false;
  configInfo: any = {};
  backendStatus: 'checking' | 'available' | 'unavailable' | 'unknown' = 'unknown';

  constructor(
    private apiConfig: ApiConfigService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.updateConfigInfo();
    this.isVisible = !environment.production; // Visible seulement en développement

    // Afficher une notification initiale sur le mode API
    this.showInitialApiModeNotification();
  }

  updateConfigInfo(): void {
    this.configInfo = this.apiConfig.getConfigInfo();
  }

  toggleMockApi(): void {
    const wasMockEnabled = this.apiConfig.isMockEnabled();
    this.apiConfig.toggleMockApi();
    this.updateConfigInfo();

    // Notification du changement
    if (wasMockEnabled) {
      this.notificationService.showRealApiEnabled();
    } else {
      this.notificationService.showMockApiEnabled();
    }
  }

  enableMockApi(): void {
    if (!this.apiConfig.isMockEnabled()) {
      this.apiConfig.enableMockApi();
      this.updateConfigInfo();
      this.notificationService.showMockApiEnabled();
    }
  }

  disableMockApi(): void {
    if (this.apiConfig.isMockEnabled()) {
      this.apiConfig.disableMockApi();
      this.updateConfigInfo();
      this.notificationService.showRealApiEnabled();
    }
  }

  async checkBackendStatus(): Promise<void> {
    this.backendStatus = 'checking';
    this.notificationService.showInfo('Vérification', 'Test de connexion au backend...');

    try {
      const isAvailable = await this.apiConfig.checkBackendConnectivity();
      this.backendStatus = isAvailable ? 'available' : 'unavailable';

      if (isAvailable) {
        this.notificationService.showSuccess('Backend disponible', 'Le serveur répond correctement');
      } else {
        this.notificationService.showWarning('Backend indisponible', 'Le serveur ne répond pas');
      }
    } catch (error) {
      this.backendStatus = 'unavailable';
      this.notificationService.showError('Erreur de connexion', 'Impossible de joindre le serveur');
    }
  }

  async autoSwitchToMock(): Promise<void> {
    this.notificationService.showInfo('Auto Switch', 'Vérification et basculement automatique...');

    await this.apiConfig.autoSwitchToMockIfNeeded();
    this.updateConfigInfo();

    if (this.apiConfig.isMockEnabled()) {
      this.notificationService.showAutoSwitchToMock();
    }
  }

  getStatusColor(): string {
    switch (this.backendStatus) {
      case 'checking': return '#ffa500';
      case 'available': return '#4caf50';
      case 'unavailable': return '#f44336';
      default: return '#9e9e9e';
    }
  }

  getStatusText(): string {
    switch (this.backendStatus) {
      case 'checking': return 'Vérification...';
      case 'available': return 'Disponible';
      case 'unavailable': return 'Indisponible';
      default: return 'Inconnu';
    }
  }

  togglePanelVisibility(): void {
    this.isVisible = !this.isVisible;
  }

  copyConfigToClipboard(): void {
    const configText = JSON.stringify(this.configInfo, null, 2);
    navigator.clipboard.writeText(configText).then(() => {
      this.notificationService.showSuccess('Copié', 'Configuration copiée dans le presse-papier');
    }).catch(err => {
      this.notificationService.showError('Erreur', 'Impossible de copier la configuration');
      console.error('Erreur lors de la copie:', err);
    });
  }

  private showInitialApiModeNotification(): void {
    // Attendre un peu pour que l'app se charge
    setTimeout(() => {
      if (this.apiConfig.isMockEnabled()) {
        this.notificationService.showMockApiEnabled();
      }
    }, 1000);
  }

  /**
   * Méthodes pour tester les notifications
   */
  testSuccessNotification(): void {
    this.notificationService.showSuccess('Test Succès', 'Ceci est une notification de succès');
  }

  testErrorNotification(): void {
    this.notificationService.showError('Test Erreur', 'Ceci est une notification d\'erreur');
  }

  testWarningNotification(): void {
    this.notificationService.showWarning('Test Avertissement', 'Ceci est une notification d\'avertissement');
  }

  testInfoNotification(): void {
    this.notificationService.showInfo('Test Info', 'Ceci est une notification d\'information');
  }

  testActionNotification(): void {
    this.notificationService.showWithAction(
      'info',
      'Test Action',
      'Notification avec bouton d\'action',
      'Cliquez-moi',
      () => {
        this.notificationService.showSuccess('Action exécutée', 'Vous avez cliqué sur le bouton !');
      }
    );
  }

  clearAllNotifications(): void {
    this.notificationService.clearAll();
  }
}
