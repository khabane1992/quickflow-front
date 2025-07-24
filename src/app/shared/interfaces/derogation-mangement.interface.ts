// src/app/shared/interfaces/dashboard.interface.ts

export enum DerogationStatus {
  DRAFT = 'DRAFT',
  PENDING = 'PENDING',
  IN_REVIEW = 'IN_REVIEW',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  IMPLEMENTED = 'IMPLEMENTED',
  CANCELLED = 'CANCELLED'
}

export enum DerogationType {
  TARIFF = 'TARIFF',
  LIMIT = 'LIMIT',
  COMMISSION = 'COMMISSION',
  PROCEDURE = 'PROCEDURE',
  OTHER = 'OTHER'
}

export interface DashboardRequestDTO {
  id: number;
  createdAt: string; // Format: "dd/MM/yyyy"
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
  proposedEffectiveDate: string; // Format: "dd/MM/yyyy"
  proposedEndDate: string; // Format: "dd/MM/yyyy"
  daysPending: number;
  actions: string;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  error?: string;
  timestamp?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

// Interface pour mapper les données vers le format du composant existant
export interface DerogationRequest {
  id: string;
  dateReception: string;
  dateCloture?: string;
  type: string;
  clientName: string;
  sabId: string;
  owner: string;
  responsable: string;
  entity: string;
  status: 'pending' | 'approved' | 'rejected' | 'draft' | 'processing';
}

// Utilitaires de mapping
export class DashboardMapper {

  static mapToDerogationRequest(dto: DashboardRequestDTO): DerogationRequest {
    return {
      id: dto.id.toString(),
      dateReception: dto.createdAt,
      dateCloture: dto.proposedEndDate,
      type: this.getDisplayDerogationType(dto.derogationType),
      clientName: dto.clientName,
      sabId: dto.sabClientId,
      owner: dto.owner,
      responsable: dto.responsable,
      entity: dto.agency,
      status: this.mapStatusToDisplayStatus(dto.status)
    };
  }

  static mapStatusToDisplayStatus(status: DerogationStatus): 'pending' | 'approved' | 'rejected' | 'draft' | 'processing' {
    switch (status) {
      case DerogationStatus.DRAFT:
        return 'draft';
      case DerogationStatus.PENDING:
        return 'pending';
      case DerogationStatus.IN_REVIEW:
        return 'processing';
      case DerogationStatus.APPROVED:
        return 'approved';
      case DerogationStatus.REJECTED:
        return 'rejected';
      case DerogationStatus.IMPLEMENTED:
        return 'approved';
      case DerogationStatus.CANCELLED:
        return 'rejected';
      default:
        return 'pending';
    }
  }

  static getDisplayDerogationType(type: DerogationType): string {
    switch (type) {
      case DerogationType.TARIFF:
        return 'Dérogation tarifaire';
      case DerogationType.LIMIT:
        return 'Dérogation de limite';
      case DerogationType.COMMISSION:
        return 'Dérogation de commission';
      case DerogationType.PROCEDURE:
        return 'Dérogation procédurale';
      case DerogationType.OTHER:
        return 'Autre dérogation';
      default:
        return 'Dérogation tarifaire';
    }
  }

  static getStatusLabel(status: DerogationStatus): string {
    switch (status) {
      case DerogationStatus.DRAFT:
        return 'Brouillon';
      case DerogationStatus.PENDING:
        return 'En attente';
      case DerogationStatus.IN_REVIEW:
        return 'En cours de traitement';
      case DerogationStatus.APPROVED:
        return 'Approuvée';
      case DerogationStatus.REJECTED:
        return 'Rejetée';
      case DerogationStatus.IMPLEMENTED:
        return 'Implémentée';
      case DerogationStatus.CANCELLED:
        return 'Annulée';
      default:
        return 'Inconnu';
    }
  }

  static getStatusClass(status: DerogationStatus): string {
    switch (status) {
      case DerogationStatus.DRAFT:
        return 'status-draft';
      case DerogationStatus.PENDING:
        return 'status-pending';
      case DerogationStatus.IN_REVIEW:
        return 'status-processing';
      case DerogationStatus.APPROVED:
      case DerogationStatus.IMPLEMENTED:
        return 'status-approved';
      case DerogationStatus.REJECTED:
      case DerogationStatus.CANCELLED:
        return 'status-rejected';
      default:
        return 'status-pending';
    }
  }
}
