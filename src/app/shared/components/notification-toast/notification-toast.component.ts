// src/app/shared/components/notification-toast/notification-toast.component.ts

import { Component, OnInit, OnDestroy } from '@angular/core';
import { Observable } from 'rxjs';
import { NotificationService, Notification } from '../../../core/services/notification.service';

@Component({
  selector: 'app-notification-toast',
  templateUrl: './notification-toast.component.html',
  styleUrls: ['./notification-toast.component.scss']
})
export class NotificationToastComponent implements OnInit, OnDestroy {

  notifications$: Observable<Notification[]>;

  constructor(private notificationService: NotificationService) {
    this.notifications$ = this.notificationService.getNotifications();
  }

  ngOnInit(): void {}

  ngOnDestroy(): void {}

  /**
   * Ferme une notification
   */
  closeNotification(id: string): void {
    this.notificationService.removeNotification(id);
  }

  /**
   * Exécute l'action d'une notification
   */
  executeAction(notification: Notification): void {
    if (notification.action) {
      notification.action.callback();
      this.closeNotification(notification.id);
    }
  }

  /**
   * Retourne la classe CSS pour le type de notification
   */
  getNotificationClass(type: string): string {
    return `notification-${type}`;
  }

  /**
   * Retourne l'icône pour le type de notification
   */
  getNotificationIcon(type: string): string {
    switch (type) {
      case 'success': return '✅';
      case 'error': return '❌';
      case 'warning': return '⚠️';
      case 'info': return 'ℹ️';
      default: return '📢';
    }
  }

  /**
   * Tracking function pour ngFor
   */
  trackByNotificationId(index: number, notification: Notification): string {
    return notification.id;
  }
}
