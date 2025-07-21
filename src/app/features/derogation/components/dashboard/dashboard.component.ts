import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DashboardService } from '../../services/dashboard/dashboard.service';
import { DashboardRequestDTO, DerogationStatus } from '../../models/dashboard/request.interface';

@Component({
  selector: 'app-derogation-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DerogationDashboardComponent implements OnInit {

  requestsToProcess$!: Observable<DashboardRequestDTO[]>;
  requestsPendingValidation$!: Observable<DashboardRequestDTO[]>;
  requestsProcessed$!: Observable<DashboardRequestDTO[]>;

  private currentUserId = 'user123';
  
  // Message de succès
  successMessage: string | null = null;

  constructor(
    private dashboardService: DashboardService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadRequests();
    this.checkSuccessMessage();
  }

  private loadRequests(): void {
    // TEMPORAIRE: Données mockées
    this.requestsToProcess$ = this.dashboardService.getMockData().pipe(
      map(requests => requests.filter(r => r.status === DerogationStatus.TO_PROCESS))
    );
    this.requestsPendingValidation$ = this.dashboardService.getMockData().pipe(
      map(requests => requests.filter(r => r.status === DerogationStatus.PENDING_VALIDATION))
    );
    this.requestsProcessed$ = this.dashboardService.getMockData().pipe(
      map(requests => requests.filter(r => r.status === DerogationStatus.PROCESSED))
    );
  }

  private checkSuccessMessage(): void {
    this.route.queryParams.subscribe(params => {
      if (params['success'] === 'true') {
        this.successMessage = params['requestId'] 
          ? `Demande ${params['requestId']} soumise avec succès !`
          : 'Demande sauvegardée avec succès !';
        
        // Supprimer le message après 5 secondes
        setTimeout(() => {
          this.successMessage = null;
        }, 5000);
        
        // Nettoyer l'URL
        this.router.navigate([], {
          relativeTo: this.route,
          queryParams: {}
        });
      }
    });
  }

  // NOUVELLE MÉTHODE: Navigation vers nouvelle demande
  onNewRequest(): void {
    this.router.navigate(['/derogation/nouvelle-demande']);
  }

  // Actions existantes
  viewRequest(request: DashboardRequestDTO): void {
    console.log('Voir la demande:', request.id);
    // Navigation vers détail ou édition
    this.router.navigate(['/derogation/edit', request.id]);
  }

  validateRequest(request: DashboardRequestDTO): void {
    console.log('Valider la demande:', request.id);
    // Logique de validation
  }

  processRequest(request: DashboardRequestDTO): void {
    console.log('Traiter la demande:', request.id);
    // Logique de traitement
  }

  // Méthodes de formatage existantes
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