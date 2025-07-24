// src/app/features/derogation/components/derogation-management/derogation-management.component.ts

import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject, forkJoin } from 'rxjs';
import { takeUntil, finalize } from 'rxjs/operators';
import {DerogationRequest} from "../../../../shared/interfaces/derogation-mangement.interface";
import {DarogationManagementService} from "../../../../core/services/derogation-management.service";

@Component({
  selector: 'app-derogation-management',
  templateUrl: './derogation-management.component.html',
  styleUrls: ['./derogation-management.component.scss']
})
export class DerogationManagementComponent implements OnInit, OnDestroy {

  // Données des tableaux
  requestsToProcess: DerogationRequest[] = [];
  requestsPendingValidation: DerogationRequest[] = [];
  requestsProcessed: DerogationRequest[] = [];

  // États de chargement
  isLoadingToProcess = false;
  isLoadingPendingValidation = false;
  isLoadingProcessed = false;

  // États d'erreur
  errorToProcess: string | null = null;
  errorPendingValidation: string | null = null;
  errorProcessed: string | null = null;

  // Filtres de recherche
  searchTermToProcess = '';
  searchTermPendingValidation = '';
  searchTermProcessed = '';

  // Données filtrées
  filteredRequestsToProcess: DerogationRequest[] = [];
  filteredRequestsPendingValidation: DerogationRequest[] = [];
  filteredRequestsProcessed: DerogationRequest[] = [];

  // Pour gérer les désabonnements
  private destroy$ = new Subject<void>();

  // ID utilisateur - à récupérer depuis le service d'authentification
  private currentUserId = 'current-user-id'; // TODO: Récupérer depuis AuthService

  constructor(private dashboardService: DarogationManagementService) {}

  ngOnInit(): void {
    this.loadAllData();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Charge toutes les données des trois sections
   */
  loadAllData(): void {
    this.loadDemandsToProcess();
    this.loadDemandsPendingValidation();
    this.loadDemandsProcessed();
  }

  /**
   * Charge les demandes à traiter
   */
  loadDemandsToProcess(): void {
    this.isLoadingToProcess = true;
    this.errorToProcess = null;

    this.dashboardService.getDemandsToProcess(this.currentUserId)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.isLoadingToProcess = false)
      )
      .subscribe({
        next: (demands) => {
          this.requestsToProcess = demands;
          this.applySearchFilterToProcess();
        },
        error: (error) => {
          this.errorToProcess = error.message;
          console.error('Erreur lors du chargement des demandes à traiter:', error);
        }
      });
  }

  /**
   * Charge les demandes en attente de validation
   */
  loadDemandsPendingValidation(): void {
    this.isLoadingPendingValidation = true;
    this.errorPendingValidation = null;

    this.dashboardService.getDemandsPendingValidation(this.currentUserId)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.isLoadingPendingValidation = false)
      )
      .subscribe({
        next: (demands) => {
          this.requestsPendingValidation = demands;
          this.applySearchFilterPendingValidation();
        },
        error: (error) => {
          this.errorPendingValidation = error.message;
          console.error('Erreur lors du chargement des demandes en attente:', error);
        }
      });
  }

  /**
   * Charge les demandes traitées
   */
  loadDemandsProcessed(): void {
    this.isLoadingProcessed = true;
    this.errorProcessed = null;

    this.dashboardService.getDemandsProcessed(this.currentUserId)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.isLoadingProcessed = false)
      )
      .subscribe({
        next: (demands) => {
          this.requestsProcessed = demands;
          this.applySearchFilterProcessed();
        },
        error: (error) => {
          this.errorProcessed = error.message;
          console.error('Erreur lors du chargement des demandes traitées:', error);
        }
      });
  }

  /**
   * Traite une demande
   */
  processRequest(request: DerogationRequest): void {
    if (!request.id) {
      console.error('ID de demande manquant');
      return;
    }

    this.dashboardService.processDemand(request.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (success) => {
          if (success) {
            // Recharger les données pour refléter les changements
            this.loadAllData();
            // Afficher un message de succès (optionnel)
            console.log('Demande traitée avec succès');
          }
        },
        error: (error) => {
          console.error('Erreur lors du traitement de la demande:', error);
          // Afficher un message d'erreur à l'utilisateur
        }
      });
  }

  /**
   * Valide une demande (approuve)
   */
  validateRequest(request: DerogationRequest): void {
    if (!request.id) {
      console.error('ID de demande manquant');
      return;
    }

    // Vous pouvez ajouter ici une boîte de dialogue de confirmation
    this.dashboardService.approveDemand(request.id, 'Demande approuvée via l\'interface')
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (success) => {
          if (success) {
            this.loadAllData();
            console.log('Demande approuvée avec succès');
          }
        },
        error: (error) => {
          console.error('Erreur lors de l\'approbation de la demande:', error);
        }
      });
  }

  /**
   * Affiche les détails d'une demande
   */
  viewRequest(request: DerogationRequest): void {
    console.log('Affichage de la demande:', request);
    // Rediriger vers une page de détails ou ouvrir un modal
    // this.router.navigate(['/derogation/details', request.id]);
  }

  /**
   * Rejette une demande
   */
  rejectRequest(request: DerogationRequest, reason?: string): void {
    if (!request.id) {
      console.error('ID de demande manquant');
      return;
    }

    this.dashboardService.rejectDemand(request.id, reason || 'Demande rejetée')
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (success) => {
          if (success) {
            this.loadAllData();
            console.log('Demande rejetée avec succès');
          }
        },
        error: (error) => {
          console.error('Erreur lors du rejet de la demande:', error);
        }
      });
  }

  /**
   * Actualise les données
   */
  refreshData(): void {
    this.loadAllData();
  }

  /**
   * Applique le filtre de recherche pour les demandes à traiter
   */
  applySearchFilterToProcess(): void {
    if (!this.searchTermToProcess.trim()) {
      this.filteredRequestsToProcess = [...this.requestsToProcess];
    } else {
      const searchTerm = this.searchTermToProcess.toLowerCase();
      this.filteredRequestsToProcess = this.requestsToProcess.filter(request =>
        request.clientName.toLowerCase().includes(searchTerm) ||
        request.sabId.toLowerCase().includes(searchTerm) ||
        request.owner.toLowerCase().includes(searchTerm) ||
        request.type.toLowerCase().includes(searchTerm)
      );
    }
  }

  /**
   * Applique le filtre de recherche pour les demandes en attente
   */
  applySearchFilterPendingValidation(): void {
    if (!this.searchTermPendingValidation.trim()) {
      this.filteredRequestsPendingValidation = [...this.requestsPendingValidation];
    } else {
      const searchTerm = this.searchTermPendingValidation.toLowerCase();
      this.filteredRequestsPendingValidation = this.requestsPendingValidation.filter(request =>
        request.clientName.toLowerCase().includes(searchTerm) ||
        request.sabId.toLowerCase().includes(searchTerm) ||
        request.owner.toLowerCase().includes(searchTerm) ||
        request.responsable.toLowerCase().includes(searchTerm) ||
        request.type.toLowerCase().includes(searchTerm)
      );
    }
  }

  /**
   * Applique le filtre de recherche pour les demandes traitées
   */
  applySearchFilterProcessed(): void {
    if (!this.searchTermProcessed.trim()) {
      this.filteredRequestsProcessed = [...this.requestsProcessed];
    } else {
      const searchTerm = this.searchTermProcessed.toLowerCase();
      this.filteredRequestsProcessed = this.requestsProcessed.filter(request =>
        request.clientName.toLowerCase().includes(searchTerm) ||
        request.sabId.toLowerCase().includes(searchTerm) ||
        request.owner.toLowerCase().includes(searchTerm) ||
        request.type.toLowerCase().includes(searchTerm)
      );
    }
  }

  /**
   * Gestion de la recherche - demandes à traiter
   */
  onSearchToProcess(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.searchTermToProcess = target.value;
    this.applySearchFilterToProcess();
  }

  /**
   * Gestion de la recherche - demandes en attente
   */
  onSearchPendingValidation(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.searchTermPendingValidation = target.value;
    this.applySearchFilterPendingValidation();
  }

  /**
   * Gestion de la recherche - demandes traitées
   */
  onSearchProcessed(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.searchTermProcessed = target.value;
    this.applySearchFilterProcessed();
  }

  /**
   * Méthodes utilitaires existantes (conservées pour compatibilité)
   */
  getStatusLabel(status: string): string {
    const labels: {[key: string]: string} = {
      'pending': 'En attente',
      'approved': 'Accordée',
      'rejected': 'Refusée',
      'draft': 'Brouillons',
      'processing': 'À traiter'
    };
    return labels[status] || status;
  }

  getStatusClass(status: string): string {
    const classes: {[key: string]: string} = {
      'pending': 'status-pending',
      'approved': 'status-approved',
      'rejected': 'status-rejected',
      'draft': 'status-draft',
      'processing': 'status-processing'
    };
    return classes[status] || '';
  }

  getInitials(name: string): string {
    if (!name) return '';
    return name.split(' ')
      .map(n => n.charAt(0))
      .join('')
      .toUpperCase()
      .substring(0, 2); // Limite à 2 caractères
  }

  /**
   * Gestion des erreurs d'affichage
   */
  hasError(): boolean {
    return !!(this.errorToProcess || this.errorPendingValidation || this.errorProcessed);
  }

  isLoading(): boolean {
    return this.isLoadingToProcess || this.isLoadingPendingValidation || this.isLoadingProcessed;
  }

  /**
   * Retry en cas d'erreur
   */
  retryLoad(section: 'toProcess' | 'pendingValidation' | 'processed'): void {
    switch (section) {
      case 'toProcess':
        this.loadDemandsToProcess();
        break;
      case 'pendingValidation':
        this.loadDemandsPendingValidation();
        break;
      case 'processed':
        this.loadDemandsProcessed();
        break;
    }
  }

  /**
   * Tracking function pour *ngFor (optimisation performance)
   */
  trackByRequestId(index: number, request: DerogationRequest): string {
    return request.id;
  }
}
