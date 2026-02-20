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





-- ============================================================
-- Script d'insertion des données: Zones, Groupes, Agences
-- Organisation: Zone -> Groupe -> Agence
-- ============================================================

-- ============================================================
-- INSERTION DES ZONES
-- ============================================================

INSERT INTO zones (id, nom, actif, director_zone_id, created_at, updated_at)
SELECT 'c0d994d3-2df1-411f-5b20-feea5ed2d43e', 'Zone Casa Ouest', TRUE, NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM zones WHERE id = 'c0d994d3-2df1-411f-5b20-feea5ed2d43e');

INSERT INTO zones (id, nom, actif, director_zone_id, created_at, updated_at)
SELECT '930022c6-f63b-8b91-4565-0995d1a14fcc', 'Zone Casa Nord', TRUE, NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM zones WHERE id = '930022c6-f63b-8b91-4565-0995d1a14fcc');

INSERT INTO zones (id, nom, actif, director_zone_id, created_at, updated_at)
SELECT 'de1b3f8b-4189-c5f3-221e-9e9e35bd095e', 'Zone Casa Centre', TRUE, NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM zones WHERE id = 'de1b3f8b-4189-c5f3-221e-9e9e35bd095e');

INSERT INTO zones (id, nom, actif, director_zone_id, created_at, updated_at)
SELECT '20cd916d-4781-0c47-db80-2b73e3de03ba', 'Région Sud', TRUE, NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM zones WHERE id = '20cd916d-4781-0c47-db80-2b73e3de03ba');

INSERT INTO zones (id, nom, actif, director_zone_id, created_at, updated_at)
SELECT 'd984e25d-c0bf-6210-bbcf-b141df34e493', 'Région Rabat-Nord', TRUE, NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM zones WHERE id = 'd984e25d-c0bf-6210-bbcf-b141df34e493');

INSERT INTO zones (id, nom, actif, director_zone_id, created_at, updated_at)
SELECT '9a02aa5e-0b50-f9b6-94f0-a917bbaea473', 'Région Est', TRUE, NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM zones WHERE id = '9a02aa5e-0b50-f9b6-94f0-a917bbaea473');

-- ============================================================
-- INSERTION DES GROUPES
-- ============================================================

-- Zone Casa Ouest
INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '89b16937-9589-6d08-4da9-c6439ff9f5f8', 'Groupe Casa Sud', TRUE, 'c0d994d3-2df1-411f-5b20-feea5ed2d43e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '89b16937-9589-6d08-4da9-c6439ff9f5f8');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '37db3dff-e35f-2b53-f67f-2d441567e1fd', 'Groupe CIL', TRUE, 'c0d994d3-2df1-411f-5b20-feea5ed2d43e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '37db3dff-e35f-2b53-f67f-2d441567e1fd');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '48b1ddf2-3bda-c947-1633-40db93772d23', 'Groupe La Colline', TRUE, 'c0d994d3-2df1-411f-5b20-feea5ed2d43e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '48b1ddf2-3bda-c947-1633-40db93772d23');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT 'f596e52d-c33d-2e2b-8007-74853580e261', 'Groupe Maarif', TRUE, 'c0d994d3-2df1-411f-5b20-feea5ed2d43e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = 'f596e52d-c33d-2e2b-8007-74853580e261');


-- Zone Casa Nord
INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '20eafbca-bf8e-ad7a-89cd-f587afbc9191', 'Groupe Ain Sebaa', TRUE, '930022c6-f63b-8b91-4565-0995d1a14fcc', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '20eafbca-bf8e-ad7a-89cd-f587afbc9191');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '1d920784-defb-4ee1-0e7f-ca93f250cf83', 'Groupe Cite Djemaa', TRUE, '930022c6-f63b-8b91-4565-0995d1a14fcc', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '1d920784-defb-4ee1-0e7f-ca93f250cf83');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '5e956ea6-1fb2-cedd-2aee-4e8049c4823f', 'Groupe Mohammedia', TRUE, '930022c6-f63b-8b91-4565-0995d1a14fcc', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '5e956ea6-1fb2-cedd-2aee-4e8049c4823f');


-- Zone Casa Centre
INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e', 'Groupe Casa Centre', TRUE, 'de1b3f8b-4189-c5f3-221e-9e9e35bd095e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', 'Groupe Casa Franceville', TRUE, 'de1b3f8b-4189-c5f3-221e-9e9e35bd095e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '8457c1f3-3c3c-52a3-9fdb-355f90a37f76');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '8f4e88b3-c4d7-f801-fb22-17c365168bfb', 'Groupe Casa Panoramique', TRUE, 'de1b3f8b-4189-c5f3-221e-9e9e35bd095e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '8f4e88b3-c4d7-f801-fb22-17c365168bfb');


-- Région Sud
INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '0a86e145-e9ab-8498-bc5f-b3d79a427dd9', 'Groupe Sud', TRUE, '20cd916d-4781-0c47-db80-2b73e3de03ba', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '0a86e145-e9ab-8498-bc5f-b3d79a427dd9');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '3dbd6847-d201-8261-1bff-f49243ee7385', 'Groupe Marrakech Ouest', TRUE, '20cd916d-4781-0c47-db80-2b73e3de03ba', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '3dbd6847-d201-8261-1bff-f49243ee7385');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '53490755-ebd5-2bef-bc0d-cb4bcdc5cd4f', 'Groupe Marrakech Extention', TRUE, '20cd916d-4781-0c47-db80-2b73e3de03ba', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '53490755-ebd5-2bef-bc0d-cb4bcdc5cd4f');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '9b059b14-c0da-06c4-140d-bee0e398fce3', 'Groupe Marrakech Centre', TRUE, '20cd916d-4781-0c47-db80-2b73e3de03ba', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '9b059b14-c0da-06c4-140d-bee0e398fce3');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT 'c3ef6cf3-751c-6622-9d43-8d7768ae7ff6', 'Groupe Agadir Centre', TRUE, '20cd916d-4781-0c47-db80-2b73e3de03ba', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = 'c3ef6cf3-751c-6622-9d43-8d7768ae7ff6');


-- Région Rabat-Nord
INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '3bc2dd6e-dd1a-0c5d-ea97-99690fc59238', 'Groupe Tétouan', TRUE, 'd984e25d-c0bf-6210-bbcf-b141df34e493', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '3bc2dd6e-dd1a-0c5d-ea97-99690fc59238');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '02cdbfd2-3e3c-b562-15a1-e657f8513c6d', 'Groupe Tanger', TRUE, 'd984e25d-c0bf-6210-bbcf-b141df34e493', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '02cdbfd2-3e3c-b562-15a1-e657f8513c6d');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '3f9834a8-224c-5da9-4567-d966457bec46', 'Groupe Salé Kénitra', TRUE, 'd984e25d-c0bf-6210-bbcf-b141df34e493', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '3f9834a8-224c-5da9-4567-d966457bec46');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '9d39c07a-8ba0-e4da-8873-62eef33b7ff1', 'Groupe Rabat Souissi', TRUE, 'd984e25d-c0bf-6210-bbcf-b141df34e493', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '9d39c07a-8ba0-e4da-8873-62eef33b7ff1');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '712940d2-dfb4-c065-dd56-fa60489b4d3b', 'Groupe Rabat Centre', TRUE, 'd984e25d-c0bf-6210-bbcf-b141df34e493', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '712940d2-dfb4-c065-dd56-fa60489b4d3b');


-- Région Est
INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT 'd93070fb-9c10-d31d-58cd-49d0e966df2b', 'Groupe Fès', TRUE, '9a02aa5e-0b50-f9b6-94f0-a917bbaea473', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = 'd93070fb-9c10-d31d-58cd-49d0e966df2b');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT 'b36187d4-0510-ebb1-0a06-b75f0f467740', 'Groupe Meknès', TRUE, '9a02aa5e-0b50-f9b6-94f0-a917bbaea473', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = 'b36187d4-0510-ebb1-0a06-b75f0f467740');

INSERT INTO groupes (id, nom, actif, zone_id, director_group_id, created_at, updated_at)
SELECT '1d310338-72fa-9af5-bb90-66c3876e4618', 'Groupe Oujda-Nador', TRUE, '9a02aa5e-0b50-f9b6-94f0-a917bbaea473', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM groupes WHERE id = '1d310338-72fa-9af5-bb90-66c3876e4618');


-- ============================================================
-- INSERTION DES AGENCES
-- ============================================================

-- Zone Casa Ouest > Groupe Casa Sud
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '6fbcb228-4822-dbd1-d8de-2490246ecb7d', 'DAR BOUAAZA', '1422', TRUE, '89b16937-9589-6d08-4da9-c6439ff9f5f8', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '6fbcb228-4822-dbd1-d8de-2490246ecb7d');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '8e03b705-3b65-fa49-705d-66d2e10e3d2e', 'CASA ERRAHMA', '1405', TRUE, '89b16937-9589-6d08-4da9-c6439ff9f5f8', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '8e03b705-3b65-fa49-705d-66d2e10e3d2e');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '788de748-86db-6044-0548-4a95d80a8f1a', 'SIDI BENNOUR', '1396', TRUE, '89b16937-9589-6d08-4da9-c6439ff9f5f8', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '788de748-86db-6044-0548-4a95d80a8f1a');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'fbfa9fb4-7618-e9b8-2879-b4480395395c', 'HAD SOUALEM', '1344', TRUE, '89b16937-9589-6d08-4da9-c6439ff9f5f8', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'fbfa9fb4-7618-e9b8-2879-b4480395395c');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '15a4f997-3136-4f15-8118-271dc12ec70b', 'AZEMMOUR', '1237', TRUE, '89b16937-9589-6d08-4da9-c6439ff9f5f8', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '15a4f997-3136-4f15-8118-271dc12ec70b');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'a67b1daa-0a19-1d42-88bc-a186ce1da300', 'EL JADIDA ROUTE DE MARRAKECH', '1271', TRUE, '89b16937-9589-6d08-4da9-c6439ff9f5f8', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'a67b1daa-0a19-1d42-88bc-a186ce1da300');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '23c97b2a-f86f-c788-3f5d-747e59b109bf', 'EL JADIDA R.S.BOUZID', '1202', TRUE, '89b16937-9589-6d08-4da9-c6439ff9f5f8', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '23c97b2a-f86f-c788-3f5d-747e59b109bf');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '5e3424e1-a215-f835-ec93-84bc57e1afe8', 'EL JADIDA MOHAMED V', '1305', TRUE, '89b16937-9589-6d08-4da9-c6439ff9f5f8', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '5e3424e1-a215-f835-ec93-84bc57e1afe8');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'a5c0ff91-49cf-6444-71b8-dcdf6953eb7a', 'EL JADIDA', '1011', TRUE, '89b16937-9589-6d08-4da9-c6439ff9f5f8', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'a5c0ff91-49cf-6444-71b8-dcdf6953eb7a');

-- Zone Casa Ouest > Groupe CIL
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '448008e6-f049-76b9-04c3-1882d175f2f9', 'CASA CIL', '1074', TRUE, '37db3dff-e35f-2b53-f67f-2d441567e1fd', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '448008e6-f049-76b9-04c3-1882d175f2f9');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'ed1b5bde-ac0d-b942-3aba-8902e4a47bf8', 'CASA GHANDI', '1091', TRUE, '37db3dff-e35f-2b53-f67f-2d441567e1fd', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'ed1b5bde-ac0d-b942-3aba-8902e4a47bf8');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'f3e442f1-bb25-03a2-bec4-49c481ea7179', 'CASA BEAUSEJOUR', '1067', TRUE, '37db3dff-e35f-2b53-f67f-2d441567e1fd', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'f3e442f1-bb25-03a2-bec4-49c481ea7179');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '59c5c862-99aa-5d95-5aa2-761e02d96850', 'CASA JARDINS D''ANFA', '1099', TRUE, '37db3dff-e35f-2b53-f67f-2d441567e1fd', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '59c5c862-99aa-5d95-5aa2-761e02d96850');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'f06b0b74-0631-9278-87ae-eb10c2ca037f', 'CASA AVIATIONS', '1098', TRUE, '37db3dff-e35f-2b53-f67f-2d441567e1fd', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'f06b0b74-0631-9278-87ae-eb10c2ca037f');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '06e93bf8-7438-2486-64ec-63df104ad227', 'CASA HAY HASSANI', '1094', TRUE, '37db3dff-e35f-2b53-f67f-2d441567e1fd', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '06e93bf8-7438-2486-64ec-63df104ad227');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '4e48b9bb-423c-2fa8-00a9-15bbcf9964bc', 'CASA OUM ERRABII', '1172', TRUE, '37db3dff-e35f-2b53-f67f-2d441567e1fd', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '4e48b9bb-423c-2fa8-00a9-15bbcf9964bc');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'caa2cb9b-a42a-85ee-74d8-cce9144d0cbf', 'CASA MAZOLA', '1232', TRUE, '37db3dff-e35f-2b53-f67f-2d441567e1fd', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'caa2cb9b-a42a-85ee-74d8-cce9144d0cbf');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '1106ca68-79a2-a445-b63c-4bd433e9d24f', 'CASA YACOUB EL MANSOUR', '1131', TRUE, '37db3dff-e35f-2b53-f67f-2d441567e1fd', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '1106ca68-79a2-a445-b63c-4bd433e9d24f');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '891284f1-42f3-cdd3-ea90-43c87d95c92e', 'CASA IBN SINA', '1234', TRUE, '37db3dff-e35f-2b53-f67f-2d441567e1fd', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '891284f1-42f3-cdd3-ea90-43c87d95c92e');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '4c1dd58e-4a8d-9db8-9de3-7dcfd9a5ab4a', 'CASA HAY ESSALAM', '1293', TRUE, '37db3dff-e35f-2b53-f67f-2d441567e1fd', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '4c1dd58e-4a8d-9db8-9de3-7dcfd9a5ab4a');

-- Zone Casa Ouest > Groupe La Colline
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'e720e1ec-7858-87fa-ea72-03ab08fb7bc6', 'CASA LA COLLINE', '1147', TRUE, '48b1ddf2-3bda-c947-1633-40db93772d23', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'e720e1ec-7858-87fa-ea72-03ab08fb7bc6');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '9c9f405c-1f1f-dbc8-8bb5-44b3ecb7bd2a', 'CASA HAY AMINE-SIDI MAAROUF', '1159', TRUE, '48b1ddf2-3bda-c947-1633-40db93772d23', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '9c9f405c-1f1f-dbc8-8bb5-44b3ecb7bd2a');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'aaf44554-f902-e832-2638-fda67d4dca26', 'CASA OULFA', '1072', TRUE, '48b1ddf2-3bda-c947-1633-40db93772d23', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'aaf44554-f902-e832-2638-fda67d4dca26');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'd10d8813-fd15-33ad-d221-8d384a6fc3dc', 'CASANEARSHORE', '1279', TRUE, '48b1ddf2-3bda-c947-1633-40db93772d23', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'd10d8813-fd15-33ad-d221-8d384a6fc3dc');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '81d7722a-9f25-7452-350d-312919f48645', 'CASA ROND POINT CHAHDIA', '1181', TRUE, '48b1ddf2-3bda-c947-1633-40db93772d23', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '81d7722a-9f25-7452-350d-312919f48645');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '3cedd11f-3345-9753-e136-3ea7ea66b262', 'CASA MANDAROUNA', '1282', TRUE, '48b1ddf2-3bda-c947-1633-40db93772d23', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '3cedd11f-3345-9753-e136-3ea7ea66b262');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'e8277908-9993-aa28-a1d2-aff13b4af403', 'CASA LES ORANGERS', '1283', TRUE, '48b1ddf2-3bda-c947-1633-40db93772d23', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'e8277908-9993-aa28-a1d2-aff13b4af403');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'ab3b1f43-827f-d6c1-6d88-61c927e486d4', 'SIDI MAAROUF METRO', '1353', TRUE, '48b1ddf2-3bda-c947-1633-40db93772d23', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'ab3b1f43-827f-d6c1-6d88-61c927e486d4');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '9409a971-4ef9-0018-ee89-a262848bafc4', 'CASA NASSIM', '1324', TRUE, '48b1ddf2-3bda-c947-1633-40db93772d23', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '9409a971-4ef9-0018-ee89-a262848bafc4');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'a38a0b76-0206-1b51-27bc-f38b8b1d4fb5', 'CASA ZOUBEIR', '1229', TRUE, '48b1ddf2-3bda-c947-1633-40db93772d23', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'a38a0b76-0206-1b51-27bc-f38b8b1d4fb5');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '3cba2106-f53c-f946-542a-133be8e8236d', 'CASA OUED SEBOU', '1222', TRUE, '48b1ddf2-3bda-c947-1633-40db93772d23', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '3cba2106-f53c-f946-542a-133be8e8236d');

-- Zone Casa Ouest > Groupe Maarif
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '81354aff-b8e9-a374-c830-da058eb8888d', 'CASA VAL D''ANFA', '1120', TRUE, 'f596e52d-c33d-2e2b-8007-74853580e261', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '81354aff-b8e9-a374-c830-da058eb8888d');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'c45d0754-76d8-2fc0-12ba-c505e4d36326', 'CASA MAARIF MOSQUEE', '1087', TRUE, 'f596e52d-c33d-2e2b-8007-74853580e261', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'c45d0754-76d8-2fc0-12ba-c505e4d36326');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'aaa2d5cb-ec1a-6e27-a630-61add7bef3bb', 'CASA MAARIF', '1007', TRUE, 'f596e52d-c33d-2e2b-8007-74853580e261', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'aaa2d5cb-ec1a-6e27-a630-61add7bef3bb');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'c2403441-830f-1a3a-ddf1-453e29d86868', 'CASA ZERKTOUNI TWIN CENTER', '1100', TRUE, 'f596e52d-c33d-2e2b-8007-74853580e261', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'c2403441-830f-1a3a-ddf1-453e29d86868');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '0ee0920b-1a5f-9c45-8d22-4232788c220e', 'CASA ROND POINT DES SPORTS', '1134', TRUE, 'f596e52d-c33d-2e2b-8007-74853580e261', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '0ee0920b-1a5f-9c45-8d22-4232788c220e');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '37b1cae6-03e7-a6f1-11bd-73a1f770fb3c', 'CASA RACINE', '1173', TRUE, 'f596e52d-c33d-2e2b-8007-74853580e261', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '37b1cae6-03e7-a6f1-11bd-73a1f770fb3c');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '57a869e3-3fc0-985c-22fa-1fe862170a7e', 'CASA NORMANDIE', '1802', TRUE, 'f596e52d-c33d-2e2b-8007-74853580e261', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '57a869e3-3fc0-985c-22fa-1fe862170a7e');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '64122873-43f5-06d7-9622-a6fc26e84737', 'CASA AHMED CHARSI', '1262', TRUE, 'f596e52d-c33d-2e2b-8007-74853580e261', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '64122873-43f5-06d7-9622-a6fc26e84737');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '6d60f652-36b4-0639-f4e2-9638b29b304a', 'CASA AL FORAT', '1287', TRUE, 'f596e52d-c33d-2e2b-8007-74853580e261', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '6d60f652-36b4-0639-f4e2-9638b29b304a');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'f765377e-ffec-9ddb-d8bf-b80071d20c17', 'CASA BORGOGNE', '1161', TRUE, 'f596e52d-c33d-2e2b-8007-74853580e261', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'f765377e-ffec-9ddb-d8bf-b80071d20c17');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '58fa2c34-12ee-3c04-44e3-603dca6894b9', 'CASA ATTABARI', '1220', TRUE, 'f596e52d-c33d-2e2b-8007-74853580e261', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '58fa2c34-12ee-3c04-44e3-603dca6894b9');

-- Zone Casa Nord > Groupe Ain Sebaa
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '2ab9fb52-eea9-00e4-f47e-108669d5ba68', 'CASA LA GIRONDE', '1004', TRUE, '20eafbca-bf8e-ad7a-89cd-f587afbc9191', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '2ab9fb52-eea9-00e4-f47e-108669d5ba68');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'e3d006ff-8998-0662-2b33-3993c16906cc', 'CASA EMILE ZOLA', '1140', TRUE, '20eafbca-bf8e-ad7a-89cd-f587afbc9191', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'e3d006ff-8998-0662-2b33-3993c16906cc');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '8ce7ad35-7b08-c6f3-dcf6-c44585233c86', 'CASA GARE', '1003', TRUE, '20eafbca-bf8e-ad7a-89cd-f587afbc9191', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '8ce7ad35-7b08-c6f3-dcf6-c44585233c86');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'c1ea6897-63fd-3f7f-d6ec-974b8550d432', 'CASA AMBASSADEUR BEN AICHA', '1249', TRUE, '20eafbca-bf8e-ad7a-89cd-f587afbc9191', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'c1ea6897-63fd-3f7f-d6ec-974b8550d432');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'ac5017f9-dede-1289-b4ee-4b09eeec42ff', 'CASA ZENATA', '1075', TRUE, '20eafbca-bf8e-ad7a-89cd-f587afbc9191', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'ac5017f9-dede-1289-b4ee-4b09eeec42ff');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '75ef5580-9510-b1aa-9b63-ba04abf678b7', 'CASA FOUARAT', '1089', TRUE, '20eafbca-bf8e-ad7a-89cd-f587afbc9191', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '75ef5580-9510-b1aa-9b63-ba04abf678b7');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'c8c3dd1f-f60b-c6b5-707d-64850fe0f1dc', 'CASA AIN BORJA', '1088', TRUE, '20eafbca-bf8e-ad7a-89cd-f587afbc9191', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'c8c3dd1f-f60b-c6b5-707d-64850fe0f1dc');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '4fc746a0-202d-921d-bbbe-a8ab4568f526', 'CASA AÏN SEBAÂ', '1080', TRUE, '20eafbca-bf8e-ad7a-89cd-f587afbc9191', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '4fc746a0-202d-921d-bbbe-a8ab4568f526');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'dcb485f7-2071-c0c9-0d55-b04d6b2f8f03', 'CASA FERNAND COSMOS', '1347', TRUE, '20eafbca-bf8e-ad7a-89cd-f587afbc9191', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'dcb485f7-2071-c0c9-0d55-b04d6b2f8f03');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '39cf0a4d-2949-79dd-a818-d38ffe9b4cef', 'CASA QOD''S NAKHIL', '1044', TRUE, '20eafbca-bf8e-ad7a-89cd-f587afbc9191', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '39cf0a4d-2949-79dd-a818-d38ffe9b4cef');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'ddc9b315-9c76-e9ee-796e-c8fc00f721e7', 'CASA SIDI BERNOUSSI', '1160', TRUE, '20eafbca-bf8e-ad7a-89cd-f587afbc9191', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'ddc9b315-9c76-e9ee-796e-c8fc00f721e7');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'd676ff2e-0f88-a7f1-c9a4-a57f93559f72', 'CASA TIZI OUSLI', '1377', TRUE, '20eafbca-bf8e-ad7a-89cd-f587afbc9191', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'd676ff2e-0f88-a7f1-c9a4-a57f93559f72');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '36c972fd-e2ca-c5ee-8aad-d933fd902d28', 'CASA AL QODS', '1169', TRUE, '20eafbca-bf8e-ad7a-89cd-f587afbc9191', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '36c972fd-e2ca-c5ee-8aad-d933fd902d28');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'd91fd19e-38fb-b444-ce10-ae4532577067', 'CASA AL HAMD', '1055', TRUE, '20eafbca-bf8e-ad7a-89cd-f587afbc9191', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'd91fd19e-38fb-b444-ce10-ae4532577067');

-- Zone Casa Nord > Groupe Cite Djemaa
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'f8c871c8-d3fb-6b4d-fb61-706ee1c03c60', 'CASA LE NIL', '1097', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'f8c871c8-d3fb-6b4d-fb61-706ee1c03c60');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '56ee3ae2-1cd0-6bf9-08c4-151b055a9260', 'CASA IFRIQUIA', '1250', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '56ee3ae2-1cd0-6bf9-08c4-151b055a9260');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'a371cb7e-793b-2828-c98d-677e39284f2b', 'CASA IBNOU LWALID', '1363', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'a371cb7e-793b-2828-c98d-677e39284f2b');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'db658264-8f21-1dd5-e776-258cdcdac64e', 'CASA MAKDAD LAHRIZI', '1162', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'db658264-8f21-1dd5-e776-258cdcdac64e');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'a86fb0eb-2c8e-f6b0-d30b-9805cf8ef8f9', 'CASA AL MOUAHIDINE', '1438', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'a86fb0eb-2c8e-f6b0-d30b-9805cf8ef8f9');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'f0c6f23e-0d91-2b80-5c75-142fd14eddf6', 'CASA SADRI', '1065', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'f0c6f23e-0d91-2b80-5c75-142fd14eddf6');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'c16a05ba-c171-df6e-8c8a-23de949a3487', 'CASA AKID ALLAM', '1156', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'c16a05ba-c171-df6e-8c8a-23de949a3487');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '13e363c6-06e8-f519-8c5a-727cf8f7d395', 'CASA JAOUHARA', '1062', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '13e363c6-06e8-f519-8c5a-727cf8f7d395');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'ef48b1a9-1682-8b62-4672-5f2a30215f36', 'CASA HAY ESSALAMA', '1186', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'ef48b1a9-1682-8b62-4672-5f2a30215f36');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'a2648782-1edb-3cc2-a9df-33f29c0f0a07', 'CASA FALAH', '1416', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'a2648782-1edb-3cc2-a9df-33f29c0f0a07');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'a3d9772c-681d-f180-1210-f9e7bf0d9dec', 'CASA ABDELKADER SAHRAOUI', '1286', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'a3d9772c-681d-f180-1210-f9e7bf0d9dec');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'f301e2ff-8af8-9b21-da1f-543b038d21f3', 'CASA ANASSI', '1382', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'f301e2ff-8af8-9b21-da1f-543b038d21f3');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '845f5cd7-b463-03ba-fc69-840b7d752cc1', 'CASA AL ADDARISSA', '1430', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '845f5cd7-b463-03ba-fc69-840b7d752cc1');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'f41b6781-ad9a-bc8e-e37f-7afd902be0b8', 'CASA OUED EDDAHAB', '1151', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'f41b6781-ad9a-bc8e-e37f-7afd902be0b8');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'fcb8651b-9a7c-0826-e0b5-6510188cff86', 'CASA CITE DJEMAA', '1084', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'fcb8651b-9a7c-0826-e0b5-6510188cff86');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '0a27c5e9-8944-17f4-caf5-0eceb2cca5b2', 'CASA SALMIA', '1066', TRUE, '1d920784-defb-4ee1-0e7f-ca93f250cf83', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '0a27c5e9-8944-17f4-caf5-0eceb2cca5b2');

-- Zone Casa Nord > Groupe Mohammedia
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '99b88f6d-7b67-cac5-3cfd-c85d6d21923e', 'MOHAMMEDIA SUCC', '1010', TRUE, '5e956ea6-1fb2-cedd-2aee-4e8049c4823f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '99b88f6d-7b67-cac5-3cfd-c85d6d21923e');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'c6f819a1-7d91-2cce-15da-013fb8935b94', 'MOHAMMEDIA KASBA', '1121', TRUE, '5e956ea6-1fb2-cedd-2aee-4e8049c4823f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'c6f819a1-7d91-2cce-15da-013fb8935b94');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '6bc8e340-25a9-c275-d3ba-fa5cec99a431', 'MOHAMMEDIA HASSAN II', '1257', TRUE, '5e956ea6-1fb2-cedd-2aee-4e8049c4823f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '6bc8e340-25a9-c275-d3ba-fa5cec99a431');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'f758682a-e38d-46c3-4f5a-99f2d2dce31b', 'MOHAMMEDIA ANFA', '1213', TRUE, '5e956ea6-1fb2-cedd-2aee-4e8049c4823f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'f758682a-e38d-46c3-4f5a-99f2d2dce31b');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '29691f3a-bd1c-3343-57ab-f20881492254', 'MOHAMMEDIA EL ALIA', '1152', TRUE, '5e956ea6-1fb2-cedd-2aee-4e8049c4823f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '29691f3a-bd1c-3343-57ab-f20881492254');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '663c8f4a-829b-d947-6146-a1b50abdc7d6', 'TIT MELLIL', '1374', TRUE, '5e956ea6-1fb2-cedd-2aee-4e8049c4823f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '663c8f4a-829b-d947-6146-a1b50abdc7d6');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '76946e2e-b4a8-a0e8-3c0a-64dc159b12e7', 'BENSLIMANE', '1313', TRUE, '5e956ea6-1fb2-cedd-2aee-4e8049c4823f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '76946e2e-b4a8-a0e8-3c0a-64dc159b12e7');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '3b96e7b1-e175-f5fa-1e7e-08aa329e6645', 'BOUZNIKA', '1410', TRUE, '5e956ea6-1fb2-cedd-2aee-4e8049c4823f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '3b96e7b1-e175-f5fa-1e7e-08aa329e6645');

-- Zone Casa Centre > Groupe Casa Centre
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'b37dd372-8e73-4f2d-b01f-e383d93ba879', 'CASA LALLA YACOUT', '1002', TRUE, 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'b37dd372-8e73-4f2d-b01f-e383d93ba879');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '9b315bc4-646c-d288-54fa-8cb53934c577', 'CASA HASSAN II', '1095', TRUE, 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '9b315bc4-646c-d288-54fa-8cb53934c577');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'cd7e11e3-e6fb-d44a-1b1c-3917c23c31ba', 'CASA MERS SULTAN', '1081', TRUE, 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'cd7e11e3-e6fb-d44a-1b1c-3917c23c31ba');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'cd7a37e8-3c03-6237-ab77-9314aa90adbc', 'CASA LIBERTE', '1153', TRUE, 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'cd7a37e8-3c03-6237-ab77-9314aa90adbc');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'f34e7212-0885-957e-1e18-a7bbfb5ac934', 'CASA MOHAMED V', '1104', TRUE, 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'f34e7212-0885-957e-1e18-a7bbfb5ac934');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'd1dafa56-5149-4985-4283-b0ea5f51dfcd', 'CASA ANFA', '1077', TRUE, 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'd1dafa56-5149-4985-4283-b0ea5f51dfcd');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'bf703aab-947f-cc9e-bf6a-18a0edaab957', 'CASA FAR', '1076', TRUE, 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'bf703aab-947f-cc9e-bf6a-18a0edaab957');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'c84be1d6-e035-3d78-ea2a-b6a35b6e751f', 'CASA MLY SMAIL', '1008', TRUE, 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'c84be1d6-e035-3d78-ea2a-b6a35b6e751f');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '6068e88e-65fe-74d7-855a-f1d3fc66d9f9', 'CASA MLAY YOUSSEF', '1103', TRUE, 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '6068e88e-65fe-74d7-855a-f1d3fc66d9f9');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'bf1dbb72-2c2b-0543-dbac-868b6ae87343', 'CASA RUE D''ALGER', '1096', TRUE, 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'bf1dbb72-2c2b-0543-dbac-868b6ae87343');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'd9545048-cc20-13cc-8bbb-164a271bdc38', 'CASA AL KHANSAA', '1157', TRUE, 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'd9545048-cc20-13cc-8bbb-164a271bdc38');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'd7494522-a03f-cb9d-998c-bea510581eb7', 'CASA ZIRAOUI', '1083', TRUE, 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'd7494522-a03f-cb9d-998c-bea510581eb7');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '76206380-2915-b06c-126f-591a37966e9b', 'CASA GAUTHIER', '1109', TRUE, 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '76206380-2915-b06c-126f-591a37966e9b');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'ffdc31e8-9174-c183-7923-5db8ed354686', 'DERB OMAR', '1216', TRUE, 'a99ccc31-f4b0-3a09-3dd3-848d14622f4e', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'ffdc31e8-9174-c183-7923-5db8ed354686');

-- Zone Casa Centre > Groupe Casa Franceville
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'fd4adf46-07a7-0b67-cdd1-5318fd70b18b', 'CASA SUD', '1180', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'fd4adf46-07a7-0b67-cdd1-5318fd70b18b');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '930e7360-1ee1-ba7d-9b4d-c64ca32b6ea4', 'CASA SIDI MAAROUF', '1006', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '930e7360-1ee1-ba7d-9b4d-c64ca32b6ea4');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '63407716-b4c4-1c4a-5f55-8e800abf97ef', 'CASA ZERKTOUNI', '1086', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '63407716-b4c4-1c4a-5f55-8e800abf97ef');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '9099628c-1c65-2311-85fe-3bd3fd1b9d31', 'CASA 2 MARS MECHOUAR', '1093', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '9099628c-1c65-2311-85fe-3bd3fd1b9d31');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '5b3ca265-8b13-af86-4f53-bbbe2741b4ab', 'CASA ABDELMOUMEM', '1139', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '5b3ca265-8b13-af86-4f53-bbbe2741b4ab');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '4610bf06-013c-cbc6-83d8-be6d0e9801e0', 'CASA PALMIERS', '1090', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '4610bf06-013c-cbc6-83d8-be6d0e9801e0');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '9109278a-4ed7-d7a6-87a1-8a51c94b80bb', 'CASA OASIS', '1092', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '9109278a-4ed7-d7a6-87a1-8a51c94b80bb');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '8fd21273-b715-e9c2-226e-7c66e3b74f67', 'CASA VAL FLEURI', '1195', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '8fd21273-b715-e9c2-226e-7c66e3b74f67');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'f42430cf-051f-d280-6100-be835c69254c', 'CASA France VILLE', '1431', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'f42430cf-051f-d280-6100-be835c69254c');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'ae713b91-dbc5-5ff8-ea7e-824cdd6b6600', 'CASA DRISSIA', '1085', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'ae713b91-dbc5-5ff8-ea7e-824cdd6b6600');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '33b960cf-8c06-444d-09e4-a6b6333395ba', 'CASA 2 MARS', '1108', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '33b960cf-8c06-444d-09e4-a6b6333395ba');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '612fdc40-7ece-f433-4bff-232cfe4e81a6', 'CASA DERB TOLBA', '1005', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '612fdc40-7ece-f433-4bff-232cfe4e81a6');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '7c808c49-230f-5fda-5e0b-251c973907ca', 'CASA BD DE SEBTA', '1245', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '7c808c49-230f-5fda-5e0b-251c973907ca');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'bb63d007-d8ec-b871-aea1-157e34e2b74f', 'CASA DERB KABIR', '1265', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'bb63d007-d8ec-b871-aea1-157e34e2b74f');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '33de3381-a850-2407-b85c-15961fe162a6', 'CASA EL FIDA', '1158', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '33de3381-a850-2407-b85c-15961fe162a6');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'e9a9f464-752f-eec8-e32d-020299c1ad8b', 'CASA HAY EL FARAH', '1215', TRUE, '8457c1f3-3c3c-52a3-9fdb-355f90a37f76', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'e9a9f464-752f-eec8-e32d-020299c1ad8b');

-- Zone Casa Centre > Groupe Casa Panoramique
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'f6909cb6-4be5-111b-d5da-2febace9d77a', 'CASA AIN CHOCK', '1191', TRUE, '8f4e88b3-c4d7-f801-fb22-17c365168bfb', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'f6909cb6-4be5-111b-d5da-2febace9d77a');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '66e5001c-c9f7-ebea-7911-68ee72c79fb2', 'CASA JNANE CALIFORNIE', '1429', TRUE, '8f4e88b3-c4d7-f801-fb22-17c365168bfb', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '66e5001c-c9f7-ebea-7911-68ee72c79fb2');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'dad8869b-5701-4c65-e879-a33d56e5a2d4', 'CASA CALIFORNIE', '1174', TRUE, '8f4e88b3-c4d7-f801-fb22-17c365168bfb', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'dad8869b-5701-4c65-e879-a33d56e5a2d4');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'b370ff50-daf4-96e1-dd48-b9736926e9c0', 'OULED SALEH', '1361', TRUE, '8f4e88b3-c4d7-f801-fb22-17c365168bfb', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'b370ff50-daf4-96e1-dd48-b9736926e9c0');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '6b51325c-5e98-7fba-a355-f14c95e09540', 'CASA PANORAMIQUE', '1427', TRUE, '8f4e88b3-c4d7-f801-fb22-17c365168bfb', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '6b51325c-5e98-7fba-a355-f14c95e09540');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '2cfaf75b-1a4f-4a6d-9164-6e267af683e2', 'CASA M''SALLAH', '1071', TRUE, '8f4e88b3-c4d7-f801-fb22-17c365168bfb', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '2cfaf75b-1a4f-4a6d-9164-6e267af683e2');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'c7e3698a-b45f-9aba-75de-78af20386e47', 'CASA HAY MOULAY ABDELLAH', '1164', TRUE, '8f4e88b3-c4d7-f801-fb22-17c365168bfb', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'c7e3698a-b45f-9aba-75de-78af20386e47');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'c4e84ef4-a084-0b61-1b6f-efb6fe36f020', 'BERRECHID', '1210', TRUE, '8f4e88b3-c4d7-f801-fb22-17c365168bfb', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'c4e84ef4-a084-0b61-1b6f-efb6fe36f020');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'ce4c3070-76c0-0dce-e29f-879202d4cc1c', 'BERRECHID ROUTE DU SUD', '1362', TRUE, '8f4e88b3-c4d7-f801-fb22-17c365168bfb', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'ce4c3070-76c0-0dce-e29f-879202d4cc1c');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'd3105b0c-38d6-6708-51b7-07aeac9cecee', 'SETTAT', '1019', TRUE, '8f4e88b3-c4d7-f801-fb22-17c365168bfb', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'd3105b0c-38d6-6708-51b7-07aeac9cecee');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'a1dc184f-c143-a6f1-7d9c-2222db88e5e6', 'BERRECHID RTE DE CASA', '1020', TRUE, '8f4e88b3-c4d7-f801-fb22-17c365168bfb', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'a1dc184f-c143-a6f1-7d9c-2222db88e5e6');

-- Région Sud > Groupe Sud
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'df6b53ea-40f9-f217-6d03-bcb80978f8a5', 'TIZNIT', '1048', TRUE, '0a86e145-e9ab-8498-bc5f-b3d79a427dd9', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'df6b53ea-40f9-f217-6d03-bcb80978f8a5');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '64f0cf6a-d519-6179-c355-e52874e24dbe', 'AIT MELLOUL', '1046', TRUE, '0a86e145-e9ab-8498-bc5f-b3d79a427dd9', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '64f0cf6a-d519-6179-c355-e52874e24dbe');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '28eb0b7f-45dd-aacb-c8cf-7e0d223f83bb', 'INEZGANE MD V', '1013', TRUE, '0a86e145-e9ab-8498-bc5f-b3d79a427dd9', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '28eb0b7f-45dd-aacb-c8cf-7e0d223f83bb');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '454ca409-8d58-5bf7-0d18-7306f2656b5f', 'OULED TEIMA', '1014', TRUE, '0a86e145-e9ab-8498-bc5f-b3d79a427dd9', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '454ca409-8d58-5bf7-0d18-7306f2656b5f');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'b49d61e9-10f2-ff00-2f8e-f244873a7b5f', 'TAROUDANT JNANE', '1228', TRUE, '0a86e145-e9ab-8498-bc5f-b3d79a427dd9', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'b49d61e9-10f2-ff00-2f8e-f244873a7b5f');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '844e63ee-a78e-cc8c-bf76-72933127e01d', 'AGADIR TIKIWINE', '1206', TRUE, '0a86e145-e9ab-8498-bc5f-b3d79a427dd9', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '844e63ee-a78e-cc8c-bf76-72933127e01d');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '0d90ac6d-8a3c-7f4e-2419-d54aca8e0f69', 'LAÂYOUNE', '1450', TRUE, '0a86e145-e9ab-8498-bc5f-b3d79a427dd9', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '0d90ac6d-8a3c-7f4e-2419-d54aca8e0f69');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'b6740717-8fc3-fb6b-8da7-1b281ef43812', 'TAROUDANT', '1045', TRUE, '0a86e145-e9ab-8498-bc5f-b3d79a427dd9', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'b6740717-8fc3-fb6b-8da7-1b281ef43812');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '19750e3b-8275-3979-5f0c-9b2c446936d8', 'DAKHLA', '1449', TRUE, '0a86e145-e9ab-8498-bc5f-b3d79a427dd9', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '19750e3b-8275-3979-5f0c-9b2c446936d8');

-- Région Sud > Groupe Marrakech Ouest
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '6220f51e-6b55-0415-2f6d-b6c9b06f91a9', 'ESSAOUIRA', '1235', TRUE, '3dbd6847-d201-8261-1bff-f49243ee7385', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '6220f51e-6b55-0415-2f6d-b6c9b06f91a9');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '0291d284-894b-1364-1ecd-994f191b2f8c', 'MARRAKECH TARGA', '1268', TRUE, '3dbd6847-d201-8261-1bff-f49243ee7385', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '0291d284-894b-1364-1ecd-994f191b2f8c');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '2dc13cff-41c8-59d5-c34e-eac043e6faf7', 'MARRAKECH MASSIRA', '1231', TRUE, '3dbd6847-d201-8261-1bff-f49243ee7385', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '2dc13cff-41c8-59d5-c34e-eac043e6faf7');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '6ce4ac7a-a423-3da1-d233-b81dfc303918', 'SAFI PLATEAU', '1133', TRUE, '3dbd6847-d201-8261-1bff-f49243ee7385', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '6ce4ac7a-a423-3da1-d233-b81dfc303918');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'ba34bc48-cdb1-562b-4b94-f5b0bf1c668a', 'MARRAKECH M''HAMID', '1358', TRUE, '3dbd6847-d201-8261-1bff-f49243ee7385', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'ba34bc48-cdb1-562b-4b94-f5b0bf1c668a');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '2455aed5-eb36-9f8b-2a4b-bd632636fd22', 'SAFI', '1021', TRUE, '3dbd6847-d201-8261-1bff-f49243ee7385', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '2455aed5-eb36-9f8b-2a4b-bd632636fd22');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '6fb46754-8efd-cc3c-3d5e-615fb52bac6e', 'MARRECH MOULAY ABDELLAH', '1198', TRUE, '3dbd6847-d201-8261-1bff-f49243ee7385', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '6fb46754-8efd-cc3c-3d5e-615fb52bac6e');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'cba041ec-bae7-e170-e251-f5dabf8aadb9', 'MARRAKECH ASSWAK ASSALAM', '1141', TRUE, '3dbd6847-d201-8261-1bff-f49243ee7385', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'cba041ec-bae7-e170-e251-f5dabf8aadb9');

-- Région Sud > Groupe Marrakech Extention
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'fba8c6d9-b16e-4ab5-05cf-8836b4f26d55', 'BENI MELLAL', '1026', TRUE, '53490755-ebd5-2bef-bc0d-cb4bcdc5cd4f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'fba8c6d9-b16e-4ab5-05cf-8836b4f26d55');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'bc1851e5-5578-0083-b4fe-e294860ecfc2', 'OUED ZEM', '1025', TRUE, '53490755-ebd5-2bef-bc0d-cb4bcdc5cd4f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'bc1851e5-5578-0083-b4fe-e294860ecfc2');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '43ad7e93-1905-2d92-d40b-333ef8b3cf40', 'SEBT OULED NEMMA', '1207', TRUE, '53490755-ebd5-2bef-bc0d-cb4bcdc5cd4f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '43ad7e93-1905-2d92-d40b-333ef8b3cf40');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'dc216e94-0134-aad5-fffb-91c9ba76647e', 'BENI MELLAL ROUTE DE MARRAKECH', '1226', TRUE, '53490755-ebd5-2bef-bc0d-cb4bcdc5cd4f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'dc216e94-0134-aad5-fffb-91c9ba76647e');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'e5da76b4-2283-f240-6a88-fdba88feefa1', 'FQUIH BENSALAH', '1204', TRUE, '53490755-ebd5-2bef-bc0d-cb4bcdc5cd4f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'e5da76b4-2283-f240-6a88-fdba88feefa1');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '500d0908-417f-3545-35c8-90b98dc3f5c3', 'KALAAT SERAGHNA', '1260', TRUE, '53490755-ebd5-2bef-bc0d-cb4bcdc5cd4f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '500d0908-417f-3545-35c8-90b98dc3f5c3');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '038c3b36-ec64-d3c4-4fc7-e48ce0021ef5', 'TINGHIR', '1434', TRUE, '53490755-ebd5-2bef-bc0d-cb4bcdc5cd4f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '038c3b36-ec64-d3c4-4fc7-e48ce0021ef5');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'f220f554-c8e7-d625-ef67-b4a92c5392b4', 'OUARZAZATE', '1138', TRUE, '53490755-ebd5-2bef-bc0d-cb4bcdc5cd4f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'f220f554-c8e7-d625-ef67-b4a92c5392b4');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '2b411015-1036-2585-77d1-3ba2b9ba875a', 'KHOURIBGA', '1028', TRUE, '53490755-ebd5-2bef-bc0d-cb4bcdc5cd4f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '2b411015-1036-2585-77d1-3ba2b9ba875a');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '191821ab-b817-4b36-5dd9-49024f16f3e5', 'KHOURIBGA ZALLAGA', '1236', TRUE, '53490755-ebd5-2bef-bc0d-cb4bcdc5cd4f', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '191821ab-b817-4b36-5dd9-49024f16f3e5');

-- Région Sud > Groupe Marrakech Centre
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '4fade7f5-58b9-9dc0-8c39-1fbd0cbb4bd1', 'MARRAKECH GUELIZ', '1106', TRUE, '9b059b14-c0da-06c4-140d-bee0e398fce3', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '4fade7f5-58b9-9dc0-8c39-1fbd0cbb4bd1');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '11bdf6b9-46be-90e0-c9a2-c87c34f054f6', 'MARRAKECH MOHAMMED V', '1300', TRUE, '9b059b14-c0da-06c4-140d-bee0e398fce3', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '11bdf6b9-46be-90e0-c9a2-c87c34f054f6');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '0b44e78e-fa57-aee3-9923-2b2825907167', 'MARRECH VICTOR HUGO', '1200', TRUE, '9b059b14-c0da-06c4-140d-bee0e398fce3', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '0b44e78e-fa57-aee3-9923-2b2825907167');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'd0d7cd8f-81f7-075c-ec40-9e95312ad33a', 'MARRAKECH ROUTE D''AGADIR', '1184', TRUE, '9b059b14-c0da-06c4-140d-bee0e398fce3', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'd0d7cd8f-81f7-075c-ec40-9e95312ad33a');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '497c5fa4-3685-341a-8e64-537391178a22', 'MARRAKECH SUCCURSALE', '1018', TRUE, '9b059b14-c0da-06c4-140d-bee0e398fce3', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '497c5fa4-3685-341a-8e64-537391178a22');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'a9e00c46-43bf-f4f2-bed1-46158c7cabad', 'MARRAKECH MEDINA', '1017', TRUE, '9b059b14-c0da-06c4-140d-bee0e398fce3', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'a9e00c46-43bf-f4f2-bed1-46158c7cabad');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'c0d1065a-a7ad-c30b-d439-946c8cb0805b', 'MARRAKECH ALLAL EL F', '1111', TRUE, '9b059b14-c0da-06c4-140d-bee0e398fce3', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'c0d1065a-a7ad-c30b-d439-946c8cb0805b');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'e335bf45-1630-895d-bb62-f49489a025b6', 'MARRAKECH SOUNBOULA', '1027', TRUE, '9b059b14-c0da-06c4-140d-bee0e398fce3', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'e335bf45-1630-895d-bb62-f49489a025b6');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '340593e0-be60-3ca3-e80c-8f00ac4e93f0', 'MARRAKECH ESSAADA', '1223', TRUE, '9b059b14-c0da-06c4-140d-bee0e398fce3', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '340593e0-be60-3ca3-e80c-8f00ac4e93f0');

-- Région Sud > Groupe Agadir Centre
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'e0975a6a-f94b-a495-62ef-20ab84dfa043', 'AGADIR KETTANI', '1012', TRUE, 'c3ef6cf3-751c-6622-9d43-8d7768ae7ff6', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'e0975a6a-f94b-a495-62ef-20ab84dfa043');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '6f07def8-b555-2d1d-23c2-60f9b2782ca8', 'AGADIR HASSAN II', '1047', TRUE, 'c3ef6cf3-751c-6622-9d43-8d7768ae7ff6', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '6f07def8-b555-2d1d-23c2-60f9b2782ca8');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'd5e21480-4102-91c5-a433-942e6a9f7497', 'AGADIR MOHAMED VI', '1294', TRUE, 'c3ef6cf3-751c-6622-9d43-8d7768ae7ff6', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'd5e21480-4102-91c5-a433-942e6a9f7497');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'fa4d287b-bb96-674c-af5b-9014b18ab1c0', 'AGADIR FEDDYA', '1144', TRUE, 'c3ef6cf3-751c-6622-9d43-8d7768ae7ff6', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'fa4d287b-bb96-674c-af5b-9014b18ab1c0');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '21b64787-9b46-ad65-a0d7-544096f2b038', 'AGADIR SALAM', '1205', TRUE, 'c3ef6cf3-751c-6622-9d43-8d7768ae7ff6', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '21b64787-9b46-ad65-a0d7-544096f2b038');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'f47ab098-5ba7-8950-4310-eeb35a2c9129', 'AGADIR TILDI', '1145', TRUE, 'c3ef6cf3-751c-6622-9d43-8d7768ae7ff6', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'f47ab098-5ba7-8950-4310-eeb35a2c9129');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '4002fb11-ff2c-4202-c405-2b3a1972929e', 'AGADIR AL MENZEH', '1230', TRUE, 'c3ef6cf3-751c-6622-9d43-8d7768ae7ff6', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '4002fb11-ff2c-4202-c405-2b3a1972929e');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'b897a442-f5d2-69dd-ca53-a34217999081', 'AGADIR BOUABID', '1175', TRUE, 'c3ef6cf3-751c-6622-9d43-8d7768ae7ff6', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'b897a442-f5d2-69dd-ca53-a34217999081');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '882c4c81-6dc3-91bf-fe63-be11e57bbe35', 'AGADIR AZIZIA', '1039', TRUE, 'c3ef6cf3-751c-6622-9d43-8d7768ae7ff6', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '882c4c81-6dc3-91bf-fe63-be11e57bbe35');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'f8ef15f6-a8bc-9960-168d-04b36bbdaad6', 'AGADIR DCHEIRA', '1199', TRUE, 'c3ef6cf3-751c-6622-9d43-8d7768ae7ff6', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'f8ef15f6-a8bc-9960-168d-04b36bbdaad6');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'eb6ba6ac-8da4-e6ff-c109-910c2856f74f', 'AGADIR ANNAHDA', '1253', TRUE, 'c3ef6cf3-751c-6622-9d43-8d7768ae7ff6', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'eb6ba6ac-8da4-e6ff-c109-910c2856f74f');

-- Région Rabat-Nord > Groupe Tétouan
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '08752982-44ea-4589-6495-3a86c24fcff7', 'TETOUAN SAFIR', '1122', TRUE, '3bc2dd6e-dd1a-0c5d-ea97-99690fc59238', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '08752982-44ea-4589-6495-3a86c24fcff7');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '11232b82-f0af-8ed9-4ea8-a49cffecf56f', 'CHEFCHAOUEN', '1225', TRUE, '3bc2dd6e-dd1a-0c5d-ea97-99690fc59238', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '11232b82-f0af-8ed9-4ea8-a49cffecf56f');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '877dfe1c-441c-0d8e-9e9b-1a16ec5796e5', 'TETOUAN', '1038', TRUE, '3bc2dd6e-dd1a-0c5d-ea97-99690fc59238', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '877dfe1c-441c-0d8e-9e9b-1a16ec5796e5');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '092346b3-d33c-96d8-3a74-5d2139a1a658', 'TANGER IBN BATOUTA', '1154', TRUE, '3bc2dd6e-dd1a-0c5d-ea97-99690fc59238', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '092346b3-d33c-96d8-3a74-5d2139a1a658');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '3375205e-b513-ad5f-3faa-c483ebd5baae', 'TETOUAN FAR', '1308', TRUE, '3bc2dd6e-dd1a-0c5d-ea97-99690fc59238', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '3375205e-b513-ad5f-3faa-c483ebd5baae');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'd991a886-caa0-bbb7-bdfa-ea1e74e87d6e', 'LARACHE', '1061', TRUE, '3bc2dd6e-dd1a-0c5d-ea97-99690fc59238', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'd991a886-caa0-bbb7-bdfa-ea1e74e87d6e');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'fd4ed68b-e399-696d-bd54-47120ff5100a', 'ASILAH', '1251', TRUE, '3bc2dd6e-dd1a-0c5d-ea97-99690fc59238', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'fd4ed68b-e399-696d-bd54-47120ff5100a');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'e11c4617-a179-454e-72a4-0dcab9f40da5', 'MARTIL', '1224', TRUE, '3bc2dd6e-dd1a-0c5d-ea97-99690fc59238', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'e11c4617-a179-454e-72a4-0dcab9f40da5');

-- Région Rabat-Nord > Groupe Tanger
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '6ae61508-dab3-9e37-4cf8-6ac11540c325', 'TANGER SUCCURSALE', '1063', TRUE, '02cdbfd2-3e3c-b562-15a1-e657f8513c6d', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '6ae61508-dab3-9e37-4cf8-6ac11540c325');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'c8bbe00a-421b-63ac-b2c9-8a9d91d4eeb6', 'TANGER PASTEUR', '1299', TRUE, '02cdbfd2-3e3c-b562-15a1-e657f8513c6d', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'c8bbe00a-421b-63ac-b2c9-8a9d91d4eeb6');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '0ea49e8f-8ef2-32e0-1336-0c758966daa8', 'TANGER P.F.', '1037', TRUE, '02cdbfd2-3e3c-b562-15a1-e657f8513c6d', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '0ea49e8f-8ef2-32e0-1336-0c758966daa8');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '0d2f0f46-669b-9ed7-08dd-f82d8867be60', 'TANGER VAL FLEURI', '1171', TRUE, '02cdbfd2-3e3c-b562-15a1-e657f8513c6d', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '0d2f0f46-669b-9ed7-08dd-f82d8867be60');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '82eccfa6-9b87-af35-b3f4-e4d772eb90e0', 'TANGER MOHAMED V', '1130', TRUE, '02cdbfd2-3e3c-b562-15a1-e657f8513c6d', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '82eccfa6-9b87-af35-b3f4-e4d772eb90e0');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'beb91770-e8c9-b3a5-9fd6-b14b709fb335', 'TANGER PCE DU KOWEIT', '1042', TRUE, '02cdbfd2-3e3c-b562-15a1-e657f8513c6d', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'beb91770-e8c9-b3a5-9fd6-b14b709fb335');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '0ab155c7-6670-bcef-cb8a-39172ba940ec', 'TANGER AIN KTIOUET', '1064', TRUE, '02cdbfd2-3e3c-b562-15a1-e657f8513c6d', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '0ab155c7-6670-bcef-cb8a-39172ba940ec');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '54a1c7ac-9038-2b2d-2d4d-17898d1dddb9', 'TANGER MY YOUSSEF', '1209', TRUE, '02cdbfd2-3e3c-b562-15a1-e657f8513c6d', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '54a1c7ac-9038-2b2d-2d4d-17898d1dddb9');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '08695174-ed38-f4c9-73e3-04441aa21aea', 'TANGER MOHAMED VI', '1170', TRUE, '02cdbfd2-3e3c-b562-15a1-e657f8513c6d', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '08695174-ed38-f4c9-73e3-04441aa21aea');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'c38123d1-23f1-3f7f-02f8-0ad3bf4b1948', 'TANGER RTE DE TETOUAN', '1246', TRUE, '02cdbfd2-3e3c-b562-15a1-e657f8513c6d', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'c38123d1-23f1-3f7f-02f8-0ad3bf4b1948');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '7aafbb3c-764f-56fc-ea4b-d0881130db2e', 'TANGER CORNICHE', '1211', TRUE, '02cdbfd2-3e3c-b562-15a1-e657f8513c6d', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '7aafbb3c-764f-56fc-ea4b-d0881130db2e');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '51496c3e-435a-00de-e42d-f0328647b9b5', 'TANGER DRISSIA', '1079', TRUE, '02cdbfd2-3e3c-b562-15a1-e657f8513c6d', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '51496c3e-435a-00de-e42d-f0328647b9b5');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '8d1d604a-9ea0-a3db-720e-930cb2a094b0', 'TANGER PLACE OUED MAKHAZINE', '1327', TRUE, '02cdbfd2-3e3c-b562-15a1-e657f8513c6d', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '8d1d604a-9ea0-a3db-720e-930cb2a094b0');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'cc1f43ec-65d6-8e4e-6baa-37e75b240715', 'TANGER CITY CENTER', '1413', TRUE, '02cdbfd2-3e3c-b562-15a1-e657f8513c6d', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'cc1f43ec-65d6-8e4e-6baa-37e75b240715');

-- Région Rabat-Nord > Groupe Salé Kénitra
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'b0551cf6-3ab9-fb5e-000f-b9d388bee3da', 'KENITRA SUCCURSALE', '1034', TRUE, '3f9834a8-224c-5da9-4567-d966457bec46', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'b0551cf6-3ab9-fb5e-000f-b9d388bee3da');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '157d8135-1b6f-9a35-6e2f-a719c398aff4', 'KENITRA VN', '1069', TRUE, '3f9834a8-224c-5da9-4567-d966457bec46', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '157d8135-1b6f-9a35-6e2f-a719c398aff4');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '157cba43-6978-925b-e206-d3799945282f', 'SALE', '1041', TRUE, '3f9834a8-224c-5da9-4567-d966457bec46', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '157cba43-6978-925b-e206-d3799945282f');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'bb1f2c13-7035-9461-8139-7a79016011a3', 'KENITRA MEDINA', '1059', TRUE, '3f9834a8-224c-5da9-4567-d966457bec46', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'bb1f2c13-7035-9461-8139-7a79016011a3');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '5a97168f-46cf-ded3-9732-e57afd5c1698', 'SALE MOHAMED V', '1183', TRUE, '3f9834a8-224c-5da9-4567-d966457bec46', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '5a97168f-46cf-ded3-9732-e57afd5c1698');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '0c24a61f-6931-10d4-c68b-a034e50db851', 'SALE HAY SALAM', '1394', TRUE, '3f9834a8-224c-5da9-4567-d966457bec46', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '0c24a61f-6931-10d4-c68b-a034e50db851');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'b4868fb1-c44e-572c-eb31-ce24bbd331e0', 'SALE TABRIQUET', '1126', TRUE, '3f9834a8-224c-5da9-4567-d966457bec46', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'b4868fb1-c44e-572c-eb31-ce24bbd331e0');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '87e3b786-8352-03a5-b346-fcbc40dbf025', 'KENITRA MED DIOURI', '1411', TRUE, '3f9834a8-224c-5da9-4567-d966457bec46', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '87e3b786-8352-03a5-b346-fcbc40dbf025');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '751c4379-98ef-2254-cfa3-5318f374c3e9', 'SIDI SLIMANE', '1035', TRUE, '3f9834a8-224c-5da9-4567-d966457bec46', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '751c4379-98ef-2254-cfa3-5318f374c3e9');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'a8893cbf-d4a7-034d-1111-6fa117ba4f71', 'SALE RTE DE KENITRA', '1201', TRUE, '3f9834a8-224c-5da9-4567-d966457bec46', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'a8893cbf-d4a7-034d-1111-6fa117ba4f71');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'd3c60172-c2cb-87a6-3875-ab7a322d156a', 'SALA AL JADIDA', '1057', TRUE, '3f9834a8-224c-5da9-4567-d966457bec46', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'd3c60172-c2cb-87a6-3875-ab7a322d156a');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'a9db184b-b8d7-0527-a006-582f045b084f', 'TIFLET', '1426', TRUE, '3f9834a8-224c-5da9-4567-d966457bec46', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'a9db184b-b8d7-0527-a006-582f045b084f');

-- Région Rabat-Nord > Groupe Rabat Souissi
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '92ee4a36-d98c-09ed-ca08-3b579317c20a', 'RABAT SUCCURSALE', '1070', TRUE, '9d39c07a-8ba0-e4da-8873-62eef33b7ff1', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '92ee4a36-d98c-09ed-ca08-3b579317c20a');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '4dc694dd-8427-dbe3-8010-337282a04b9b', 'RABAT HAY RYAD', '1127', TRUE, '9d39c07a-8ba0-e4da-8873-62eef33b7ff1', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '4dc694dd-8427-dbe3-8010-337282a04b9b');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '6ad76138-1f0a-6b10-5a82-e1eec9a3d7cb', 'RABAT 16 NOVEMBRE', '1124', TRUE, '9d39c07a-8ba0-e4da-8873-62eef33b7ff1', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '6ad76138-1f0a-6b10-5a82-e1eec9a3d7cb');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '78fb30c2-0984-4131-04c1-e155fdb90798', 'RABAT SOUISSI', '1123', TRUE, '9d39c07a-8ba0-e4da-8873-62eef33b7ff1', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '78fb30c2-0984-4131-04c1-e155fdb90798');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '1c2f365e-0d82-8c14-1495-ffeb40e759d0', 'TEMARA', '1058', TRUE, '9d39c07a-8ba0-e4da-8873-62eef33b7ff1', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '1c2f365e-0d82-8c14-1495-ffeb40e759d0');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'b47bac36-8720-c313-8886-dcbfa5d92b5b', 'RYAD NAKHIL', '1214', TRUE, '9d39c07a-8ba0-e4da-8873-62eef33b7ff1', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'b47bac36-8720-c313-8886-dcbfa5d92b5b');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'aca4de8d-746b-743e-9702-502abf217dba', 'TEMARA HASSAN II', '1176', TRUE, '9d39c07a-8ba0-e4da-8873-62eef33b7ff1', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'aca4de8d-746b-743e-9702-502abf217dba');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '50cbbb90-eafc-269c-06df-9ae38516b091', 'RABAT EL MENZEH', '1177', TRUE, '9d39c07a-8ba0-e4da-8873-62eef33b7ff1', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '50cbbb90-eafc-269c-06df-9ae38516b091');

-- Région Rabat-Nord > Groupe Rabat Centre
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '253f3ee9-3333-c5ab-81cf-f3a24c919c9a', 'RABAT HASSAN II', '1033', TRUE, '712940d2-dfb4-c065-dd56-fa60489b4d3b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '253f3ee9-3333-c5ab-81cf-f3a24c919c9a');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '45d71f26-970a-ed9a-f5a8-2c4d0aa6fed5', 'RABAT VN', '1032', TRUE, '712940d2-dfb4-c065-dd56-fa60489b4d3b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '45d71f26-970a-ed9a-f5a8-2c4d0aa6fed5');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'ace5e3d3-c4c7-380f-b506-9a3b884e4b66', 'RABAT AGDAL', '1043', TRUE, '712940d2-dfb4-c065-dd56-fa60489b4d3b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'ace5e3d3-c4c7-380f-b506-9a3b884e4b66');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '69c0a085-673f-1770-f224-5329254f0ad2', 'RABAT AV ABTAL', '1309', TRUE, '712940d2-dfb4-c065-dd56-fa60489b4d3b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '69c0a085-673f-1770-f224-5329254f0ad2');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '67ca21c0-c713-1388-e47f-56d1bdb7885f', 'RABAT ABA', '1040', TRUE, '712940d2-dfb4-c065-dd56-fa60489b4d3b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '67ca21c0-c713-1388-e47f-56d1bdb7885f');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'edef68b5-0ea4-c7c5-5c36-5dd390a346f3', 'RABAT OUDAYA', '1194', TRUE, '712940d2-dfb4-c065-dd56-fa60489b4d3b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'edef68b5-0ea4-c7c5-5c36-5dd390a346f3');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '8078bd80-2e9f-025a-7a53-425ee1a3bddf', 'RABAT TOUR HASSAN', '1125', TRUE, '712940d2-dfb4-c065-dd56-fa60489b4d3b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '8078bd80-2e9f-025a-7a53-425ee1a3bddf');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '9e54de88-6fc8-61d1-3825-2921fc9ddaee', 'RABAT MAMOUNIA', '1110', TRUE, '712940d2-dfb4-c065-dd56-fa60489b4d3b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '9e54de88-6fc8-61d1-3825-2921fc9ddaee');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '17f2ce9b-db97-b452-be84-09b815bfcd7e', 'RABAT MADAGASCAR', '1441', TRUE, '712940d2-dfb4-c065-dd56-fa60489b4d3b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '17f2ce9b-db97-b452-be84-09b815bfcd7e');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '4626463d-ba94-3e2f-3ade-32e5059ed23d', 'RABAT P. LUMUMBA', '1372', TRUE, '712940d2-dfb4-c065-dd56-fa60489b4d3b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '4626463d-ba94-3e2f-3ade-32e5059ed23d');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '90e233c5-08d9-6af6-8972-55625a88d9ab', 'RABAT 7° ART', '1298', TRUE, '712940d2-dfb4-c065-dd56-fa60489b4d3b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '90e233c5-08d9-6af6-8972-55625a88d9ab');

-- Région Est > Groupe Fès
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '0b961cf9-40bd-b103-5385-150086d81026', 'FES SUCCURSALE', '1056', TRUE, 'd93070fb-9c10-d31d-58cd-49d0e966df2b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '0b961cf9-40bd-b103-5385-150086d81026');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '29b7b292-a073-d27b-e462-30ae293e1b06', 'FES VN', '1015', TRUE, 'd93070fb-9c10-d31d-58cd-49d0e966df2b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '29b7b292-a073-d27b-e462-30ae293e1b06');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '391c6a8d-6d37-aac1-d628-78425cebc9f1', 'FES ANAS', '1107', TRUE, 'd93070fb-9c10-d31d-58cd-49d0e966df2b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '391c6a8d-6d37-aac1-d628-78425cebc9f1');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '59fe9704-c850-c2a8-ce5a-669c7e923ea3', 'FES MOULAY RACHID', '1312', TRUE, 'd93070fb-9c10-d31d-58cd-49d0e966df2b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '59fe9704-c850-c2a8-ce5a-669c7e923ea3');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '622054b4-5b98-2394-4ed6-f7a43d366972', 'FES ALLAL BEN ABDELLAH', '1227', TRUE, 'd93070fb-9c10-d31d-58cd-49d0e966df2b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '622054b4-5b98-2394-4ed6-f7a43d366972');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'e3f04365-ac0a-5a6e-e9f7-55ae7f4f3799', 'FES ABOU OUBEIDA', '1135', TRUE, 'd93070fb-9c10-d31d-58cd-49d0e966df2b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'e3f04365-ac0a-5a6e-e9f7-55ae7f4f3799');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'e0815427-2fbe-4307-0795-75d7cca27a9e', 'FES SAISS', '1301', TRUE, 'd93070fb-9c10-d31d-58cd-49d0e966df2b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'e0815427-2fbe-4307-0795-75d7cca27a9e');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'a0bda21b-3b5f-0e55-1b56-274731143ecc', 'FES NARJISS', '1219', TRUE, 'd93070fb-9c10-d31d-58cd-49d0e966df2b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'a0bda21b-3b5f-0e55-1b56-274731143ecc');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'd090b117-729b-8e1b-ce0e-16445cd89b83', 'TAZA', '1136', TRUE, 'd93070fb-9c10-d31d-58cd-49d0e966df2b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'd090b117-729b-8e1b-ce0e-16445cd89b83');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '179cf0ba-0cea-38c3-161f-5ec5a311e775', 'TAOUNATE', '1274', TRUE, 'd93070fb-9c10-d31d-58cd-49d0e966df2b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '179cf0ba-0cea-38c3-161f-5ec5a311e775');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'f909674e-89d4-a611-de09-27e7460d79a4', 'SEFROU', '1359', TRUE, 'd93070fb-9c10-d31d-58cd-49d0e966df2b', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'f909674e-89d4-a611-de09-27e7460d79a4');

-- Région Est > Groupe Meknès
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'fa175c76-39bf-ffe6-4ee4-e10a08e5b13b', 'MEKNES VN', '1022', TRUE, 'b36187d4-0510-ebb1-0a06-b75f0f467740', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'fa175c76-39bf-ffe6-4ee4-e10a08e5b13b');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '249fb979-e9e3-88f3-f663-1dd4dacb4181', 'MIDELT', '1024', TRUE, 'b36187d4-0510-ebb1-0a06-b75f0f467740', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '249fb979-e9e3-88f3-f663-1dd4dacb4181');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '6563b53c-e757-b289-1a53-996abd50e789', 'MEKNES NEHRU', '1303', TRUE, 'b36187d4-0510-ebb1-0a06-b75f0f467740', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '6563b53c-e757-b289-1a53-996abd50e789');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '2818c5b4-92fc-c1e4-a07e-7658f623d288', 'MEKNES PLAISANCE', '1051', TRUE, 'b36187d4-0510-ebb1-0a06-b75f0f467740', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '2818c5b4-92fc-c1e4-a07e-7658f623d288');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '3490253f-84b4-7867-dc94-a42e9a414dfe', 'MEKNES BAB MANSOUR', '1115', TRUE, 'b36187d4-0510-ebb1-0a06-b75f0f467740', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '3490253f-84b4-7867-dc94-a42e9a414dfe');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'ce7f3264-c493-8e59-e2b0-c25b41d20b5e', 'MEKNES SIDI SAID', '1196', TRUE, 'b36187d4-0510-ebb1-0a06-b75f0f467740', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'ce7f3264-c493-8e59-e2b0-c25b41d20b5e');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'e8d90b40-94cb-635a-043d-072ae4cc21d8', 'ERRACHIDIA', '1166', TRUE, 'b36187d4-0510-ebb1-0a06-b75f0f467740', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'e8d90b40-94cb-635a-043d-072ae4cc21d8');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'bb62f3b2-b555-8892-12c6-81b4197d4dbb', 'SIDI KACEM', '1218', TRUE, 'b36187d4-0510-ebb1-0a06-b75f0f467740', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'bb62f3b2-b555-8892-12c6-81b4197d4dbb');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '2b5b3ab3-abe1-f4db-8337-c36729c487f9', 'MEKNES MARJANE', '1203', TRUE, 'b36187d4-0510-ebb1-0a06-b75f0f467740', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '2b5b3ab3-abe1-f4db-8337-c36729c487f9');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '59781f37-e271-809a-8ccd-02ce5843ce05', 'KHEMISSAT', '1329', TRUE, 'b36187d4-0510-ebb1-0a06-b75f0f467740', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '59781f37-e271-809a-8ccd-02ce5843ce05');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '8ef46353-68fe-35ed-25e3-9e9e39358551', 'KHENIFRA', '1273', TRUE, 'b36187d4-0510-ebb1-0a06-b75f0f467740', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '8ef46353-68fe-35ed-25e3-9e9e39358551');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '35a26f6f-771a-a7c4-bc8b-bb230e52c062', 'AZROU', '1399', TRUE, 'b36187d4-0510-ebb1-0a06-b75f0f467740', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '35a26f6f-771a-a7c4-bc8b-bb230e52c062');

-- Région Est > Groupe Oujda-Nador
INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'ee920770-1d6e-f777-0243-aeee067a6f97', 'OUJDA', '1029', TRUE, '1d310338-72fa-9af5-bb90-66c3876e4618', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'ee920770-1d6e-f777-0243-aeee067a6f97');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '6409dba6-434a-38ac-6926-1277938c1653', 'GUERCIF', '1379', TRUE, '1d310338-72fa-9af5-bb90-66c3876e4618', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '6409dba6-434a-38ac-6926-1277938c1653');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'df4d04a3-1b54-bf0a-1361-9a8041efbcd0', 'NADOR', '1031', TRUE, '1d310338-72fa-9af5-bb90-66c3876e4618', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'df4d04a3-1b54-bf0a-1361-9a8041efbcd0');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '8a41d3ea-477d-eac8-cdcd-c874b1b0b0a3', 'BERKANE', '1030', TRUE, '1d310338-72fa-9af5-bb90-66c3876e4618', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '8a41d3ea-477d-eac8-cdcd-c874b1b0b0a3');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '67a63280-fe4e-94c6-bc58-1aff3e103ced', 'OUJDA MOHAMED V', '1302', TRUE, '1d310338-72fa-9af5-bb90-66c3876e4618', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '67a63280-fe4e-94c6-bc58-1aff3e103ced');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '59edf4ce-013e-8655-c952-bcad562444bb', 'NADOR OULED MIMOUN', '1132', TRUE, '1d310338-72fa-9af5-bb90-66c3876e4618', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '59edf4ce-013e-8655-c952-bcad562444bb');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '7cb3d2eb-3b53-f6bf-60bd-b376b3c98c8b', 'AL HOCEIMA', '1128', TRUE, '1d310338-72fa-9af5-bb90-66c3876e4618', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '7cb3d2eb-3b53-f6bf-60bd-b376b3c98c8b');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '0e4a6fcd-4161-d6fb-937a-ec5457630ef2', 'DRIOUECH', '1424', TRUE, '1d310338-72fa-9af5-bb90-66c3876e4618', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '0e4a6fcd-4161-d6fb-937a-ec5457630ef2');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'ac6e2110-d2b0-d455-78ff-81cb9fbf78a9', 'OUJDA LAZARET', '1375', TRUE, '1d310338-72fa-9af5-bb90-66c3876e4618', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'ac6e2110-d2b0-d455-78ff-81cb9fbf78a9');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT '27a47f33-7303-e3f7-ec63-ee106552a07c', 'MONT ARRUIT', '1179', TRUE, '1d310338-72fa-9af5-bb90-66c3876e4618', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = '27a47f33-7303-e3f7-ec63-ee106552a07c');

INSERT INTO agences (id, nom, code, actif, groupe_id, director_agency_id, created_at, updated_at)
SELECT 'a2b05bbe-5105-0613-9cae-057cd51e42c0', 'TAOURIRT', '1189', TRUE, '1d310338-72fa-9af5-bb90-66c3876e4618', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM agences WHERE id = 'a2b05bbe-5105-0613-9cae-057cd51e42c0');
