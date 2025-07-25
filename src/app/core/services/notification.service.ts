// src/app/core/services/notification.service.ts

import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Notification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message: string;
  duration?: number; // en millisecondes
  persistent?: boolean; // ne disparaît pas automatiquement
  action?: {
    label: string;
    callback: () => void;
  };
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private notifications$ = new BehaviorSubject<Notification[]>([]);
  private notificationId = 0;

  constructor() {}

  /**
   * Observable des notifications actives
   */
  getNotifications(): Observable<Notification[]> {
    return this.notifications$.asObservable();
  }

  /**
   * Affiche une notification de succès
   */
  showSuccess(title: string, message: string, duration: number = 5000): string {
    return this.addNotification({
      type: 'success',
      title,
      message,
      duration
    });
  }

  /**
   * Affiche une notification d'erreur
   */
  showError(title: string, message: string, persistent: boolean = false): string {
    return this.addNotification({
      type: 'error',
      title,
      message,
      persistent,
      duration: persistent ? undefined : 8000
    });
  }

  /**
   * Affiche une notification d'avertissement
   */
  showWarning(title: string, message: string, duration: number = 6000): string {
    return this.addNotification({
      type: 'warning',
      title,
      message,
      duration
    });
  }

  /**
   * Affiche une notification d'information
   */
  showInfo(title: string, message: string, duration: number = 4000): string {
    return this.addNotification({
      type: 'info',
      title,
      message,
      duration
    });
  }

  /**
   * Affiche une notification avec une action
   */
  showWithAction(
    type: Notification['type'], 
    title: string, 
    message: string, 
    actionLabel: string, 
    actionCallback: () => void,
    duration: number = 10000
  ): string {
    return this.addNotification({
      type,
      title,
      message,
      duration,
      action: {
        label: actionLabel,
        callback: actionCallback
      }
    });
  }

  /**
   * Notifications spécifiques pour l'API Mock
   */
  showMockApiEnabled(): string {
    return this.showWarning(
      'Mode Mock API',
      'Application en mode simulation - Les données ne sont pas réelles',
      8000
    );
  }

  showRealApiEnabled(): string {
    return this.showSuccess(
      'Mode Real API',
      'Application connectée au serveur réel',
      5000
    );
  }

  showBackendUnavailable(switchToMockCallback: () => void): string {
    return this.showWithAction(
      'error',
      'Backend indisponible',
      'Le serveur ne répond pas. Voulez-vous basculer en mode mock ?',
      'Basculer vers Mock',
      switchToMockCallback,
      0 // Notification persistante
    );
  }

  showAutoSwitchToMock(): string {
    return this.showInfo(
      'Basculement automatique',
      'Application basculée en mode mock car le backend est indisponible',
      7000
    );
  }

  /**
   * Ajoute une notification à la liste
   */
  private addNotification(notification: Omit<Notification, 'id'>): string {
    const id = `notification-${++this.notificationId}`;
    const newNotification: Notification = {
      ...notification,
      id
    };

    const currentNotifications = this.notifications$.value;
    this.notifications$.next([...currentNotifications, newNotification]);

    // Programmer la suppression automatique si une durée est spécifiée
    if (notification.duration && notification.duration > 0) {
      setTimeout(() => {
        this.removeNotification(id);
      }, notification.duration);
    }

    return id;
  }

  /**
   * Supprime une notification par son ID
   */
  removeNotification(id: string): void {
    const currentNotifications = this.notifications$.value;
    const filteredNotifications = currentNotifications.filter(n => n.id !== id);
    this.notifications$.next(filteredNotifications);
  }

  /**
   * Supprime toutes les notifications
   */
  clearAll(): void {
    this.notifications$.next([]);
  }

  /**
   * Supprime toutes les notifications d'un type spécifique
   */
  clearByType(type: Notification['type']): void {
    const currentNotifications = this.notifications$.value;
    const filteredNotifications = currentNotifications.filter(n => n.type !== type);
    this.notifications$.next(filteredNotifications);
  }

  /**
   * Met à jour une notification existante
   */
  updateNotification(id: string, updates: Partial<Omit<Notification, 'id'>>): void {
    const currentNotifications = this.notifications$.value;
    const notificationIndex = currentNotifications.findIndex(n => n.id === id);
    
    if (notificationIndex !== -1) {
      const updatedNotifications = [...currentNotifications];
      updatedNotifications[notificationIndex] = {
        ...updatedNotifications[notificationIndex],
        ...updates
      };
      this.notifications$.next(updatedNotifications);
    }
  }

  /**
   * Obtient le nombre de notifications actives
   */
  getNotificationCount(): number {
    return this.notifications$.value.length;
  }

  /**
   * Vérifie s'il y a des notifications d'un type spécifique
   */
  hasNotificationsOfType(type: Notification['type']): boolean {
    return this.notifications$.value.some(n => n.type === type);
  }
}
