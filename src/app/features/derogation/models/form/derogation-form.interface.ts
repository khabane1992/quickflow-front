export interface DerogationFormData {
  idSab: string;
  nombreComptes: number | null;
  nom: string;
  prenom: string;
  businessLine: string;
  segment: string;
  typeDerogation: string;
  codeCommercial: string;
  justificationStandard: string;
  devise: string;
  dateEffet: string;
  dateFin: string;
  tauxMontantDerogation: string;
  tiers: string;
  convention: string;
  employeur: string;
  salaire: number | null;
  fnb: number | null;
  commentaire: string;
  documents: File[];
}

export interface BusinessLineOption {
  value: string;
  label: string;
}

export interface SegmentOption {
  value: string;
  label: string;
}

export interface TypeDerogationOption {
  value: string;
  label: string;
}

export interface DeviseOption {
  value: string;
  label: string;
}

// Options pour les dropdowns
export const BUSINESS_LINES: BusinessLineOption[] = [
  { value: 'retail', label: 'Retail Banking' },
  { value: 'corporate', label: 'Corporate Banking' },
  { value: 'investment', label: 'Investment Banking' },
  { value: 'private', label: 'Private Banking' }
];

export const SEGMENTS: SegmentOption[] = [
  { value: 'particuliers', label: 'Particuliers' },
  { value: 'professionnels', label: 'Professionnels' },
  { value: 'entreprises', label: 'Entreprises' },
  { value: 'institutionnels', label: 'Institutionnels' }
];

export const TYPES_DEROGATION: TypeDerogationOption[] = [
  { value: 'navigation_logiciels', label: 'Navigation logiciels' },
  { value: 'droit_diffusion', label: 'Droit de diffusion' },
  { value: 'donnees_tiers', label: 'Données tiers' },
  { value: 'autre', label: 'Autre' }
];

export const DEVISES: DeviseOption[] = [
  { value: 'EUR', label: 'Euro (EUR)' },
  { value: 'USD', label: 'Dollar US (USD)' },
  { value: 'GBP', label: 'Livre Sterling (GBP)' },
  { value: 'CHF', label: 'Franc Suisse (CHF)' }
];