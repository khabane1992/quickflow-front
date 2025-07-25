// src/app/features/derogation/models/dashboard/request.interface.ts

export enum DemandsType {
  TO_PROCESS = 'TO_PROCESS',
  PENDING_VALIDATION = 'PENDING_VALIDATION',
  PROCESSED = 'PROCESSED',
  ALL = 'ALL'
}

export enum DerogationStatus {
  DRAFT = 'DRAFT',
  PENDING = 'PENDING',
  TO_PROCESS = 'TO_PROCESS',
  IN_REVIEW = 'IN_REVIEW',
  PENDING_VALIDATION = 'PENDING_VALIDATION',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  IMPLEMENTED = 'IMPLEMENTED',
  CANCELLED = 'CANCELLED',
  PROCESSED = 'PROCESSED'
}

export enum DerogationType {
  NAVIGATION_LOGICIELS = 'NAVIGATION_LOGICIELS',
  DROIT_DIFFUSION = 'DROIT_DIFFUSION',
  DONNEES_TIERS = 'DONNEES_TIERS',
  TARIFF = 'TARIFF',
  LIMIT = 'LIMIT',
  COMMISSION = 'COMMISSION',
  PROCEDURE = 'PROCEDURE',
  OTHER = 'OTHER'
}

export interface DashboardRequestDTO {
  id: number;
  createdAt: string;
  requestType: string;
  clientName: string;
  sabClientId: string;
  owner: string;
  responsable: string;
  agency: string;
  proposedStatus: string;
  status: DerogationStatus;
  derogationType: DerogationType;
  derogationValue: number;
  proposedEffectiveDate: string;
  proposedEndDate: string;
  daysPending: number;
  actions: string;
}

// Interface pour les statistiques du dashboard
export interface DashboardStats {
  totalRequests: number;
  pendingRequests: number;
  inReviewRequests: number;
  approvedRequests: number;
  rejectedRequests: number;
  draftRequests: number;
  averageProcessingTime: number;
  totalValue: number;
}

// Interface pour les filtres de recherche
export interface SearchFilters {
  status?: DerogationStatus;
  derogationType?: DerogationType;
  agency?: string;
  owner?: string;
  dateFrom?: string;
  dateTo?: string;
  minValue?: number;
  maxValue?: number;
}

// Interface pour les paramètres de pagination
export interface PaginationParams {
  page: number;
  size: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}

// Interface pour les actions sur les demandes
export interface DemandAction {
  type: 'APPROVE' | 'REJECT' | 'PROCESS' | 'VIEW' | 'EDIT' | 'DELETE';
  demandId: string;
  comments?: string;
  reason?: string;
}

// Interface pour la création d'une nouvelle demande
export interface CreateDemandRequest {
  requestType: string;
  clientName: string;
  sabClientId: string;
  owner: string;
  responsable: string;
  agency: string;
  derogationType: DerogationType;
  derogationValue: number;
  proposedEffectiveDate: string;
  proposedEndDate: string;
  comments?: string;
  attachments?: File[];
}

// Interface pour la mise à jour d'une demande
export interface UpdateDemandRequest {
  id: number;
  status?: DerogationStatus;
  comments?: string;
  proposedEffectiveDate?: string;
  proposedEndDate?: string;
  derogationValue?: number;
}
