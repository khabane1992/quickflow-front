import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DashboardRequestDTO, DerogationStatus } from '../models/request.interface';
import { RequestService } from '../core/services/request.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  requestsToProcess$!: Observable<DashboardRequestDTO[]>;
  requestsPendingValidation$!: Observable<DashboardRequestDTO[]>;
  requestsProcessed$!: Observable<DashboardRequestDTO[]>;

  private currentUserId = 'user123';

  constructor(private requestService: RequestService) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  private loadRequests(): void {
    // TEMPORAIRE: Utiliser les données mockées
    this.requestsToProcess$ = this.requestService.getMockData().pipe(
      map(requests => requests.filter(r => r.status === DerogationStatus.TO_PROCESS))
    );
    this.requestsPendingValidation$ = this.requestService.getMockData().pipe(
      map(requests => requests.filter(r => r.status === DerogationStatus.PENDING_VALIDATION))
    );
    this.requestsProcessed$ = this.requestService.getMockData().pipe(
      map(requests => requests.filter(r => r.status === DerogationStatus.PROCESSED))
    );

    // QUAND LE BACKEND EST PRÊT, remplacez par :
    // this.requestsToProcess$ = this.requestService.getDemandsToProcess(this.currentUserId);
    // this.requestsPendingValidation$ = this.requestService.getDemandsPendingValidation(this.currentUserId);
    // this.requestsProcessed$ = this.requestService.getDemandsProcessed(this.currentUserId);
  }

  // Actions
  viewRequest(request: DashboardRequestDTO): void {
    console.log('Voir la demande:', request.id);
  }

  validateRequest(request: DashboardRequestDTO): void {
    console.log('Valider la demande:', request.id);
  }

  processRequest(request: DashboardRequestDTO): void {
    console.log('Traiter la demande:', request.id);
  }

  // Formatage
  formatPrice(value: number): string {
    if (!value && value !== 0) return '0 €';
    return Math.round(value).toLocaleString('fr-FR') + ' €';
  }

  formatDays(days: number): string {
    if (!days && days !== 0) return '0 jour';
    return days + (days <= 1 ? ' jour' : ' jours');
  }

  getPriorityClass(daysPending: number): string {
    if (daysPending > 5) return 'priority-urgent';
    if (daysPending > 3) return 'priority-high';
    if (daysPending > 1) return 'priority-medium';
    return 'priority-low';
  }

  getStatusClass(status: DerogationStatus): string {
    switch(status) {
      case DerogationStatus.TO_PROCESS: return 'status-to-process';
      case DerogationStatus.PENDING_VALIDATION: return 'status-pending';
      case DerogationStatus.PROCESSED: return 'status-processed';
      case DerogationStatus.REJECTED: return 'status-rejected';
      default: return '';
    }
  }
}