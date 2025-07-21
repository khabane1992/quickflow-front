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

export enum DerogationStatus {
  TO_PROCESS = 'TO_PROCESS',
  PENDING_VALIDATION = 'PENDING_VALIDATION',
  PROCESSED = 'PROCESSED',
  REJECTED = 'REJECTED'
}

export enum DerogationType {
  NAVIGATION_LOGICIELS = 'Navigation logiciels',
  DROIT_DIFFUSION = 'Droit de diffusion', 
  DONNEES_TIERS = 'Données tiers',
  AUTRE = 'Autre'
}

export enum DemandsType {
  ALL = 'ALL',
  TO_PROCESS = 'TO_PROCESS',
  PENDING_VALIDATION = 'PENDING_VALIDATION',
  PROCESSED = 'PROCESSED'
}