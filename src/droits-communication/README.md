# Microservice — Droits de Communication

Backend Spring Boot du module **Droits de Communication** (maquette PO).
Stack : Java 17, Spring Boot 3.2.5, Spring Data JPA, MapStruct, Lombok, PostgreSQL (H2 en dev).

> Livraison **par phases**. Ce dépôt couvre aujourd'hui la **Phase 1**.
> Le plan complet (Phases 2 & 3) est décrit dans `/.claude/plans/` (Spring Batch → SAB,
> puis orchestration du flow de validation par **Camunda 7 externe** via REST/OpenAPI).

## Périmètre par phase

| Phase | Contenu | Statut |
|---|---|---|
| **1** | Création d'une demande (référence fichier) + listing "Mes demandes" | ✅ livré |
| **2** | Parsing .xlsx → table tampon → **Spring Batch** (chunks) → **SAB** (core banking) | ✅ livré (SAB stub) |
| **3** | Flow de validation orchestré par **Camunda 7** (moteur externe, API REST) | ✅ livré (Camunda stub) |

> Les intégrations externes (MS Document, SAB, Camunda) sont derrière des **ports**
> avec des **stubs** : tout tourne en local. On branche les vrais clients (client
> OpenAPI MS Document, appel SAB, REST Camunda) quand specs/URLs sont disponibles.

## Architecture (structure Facade)

```
controller  → expose l'API REST (validation @Valid)
facade      → orchestre mapping + service
service     → logique métier + validation des référentiels
repository  → accès données (JPA)
entity      → modèle persisté
dto         → Request / Response / Summary / StatutCount / ReferentielItem
mapper      → MapStruct entité <-> DTO
referentiel → Organisme (table) + ReferentielService
enums       → CanalDemande, TypeDemande, StatutDemande
exception   → handler global + erreurs métier
config      → auditing JPA + seed référentiels
```

## Modèle (Phase 1)

| Élément | Choix | Raison |
|---|---|---|
| Canal de la demande | enum `CanalDemande` (Mail / Courrier) | référentiel stable, maquette = 2 valeurs |
| Type de demande | `Set<TypeDemande>` (enum) | multi-sélection de la maquette |
| Organisme | table `ref_organisme` | référentiel évolutif |
| Complément d'adresse | **texte libre** | la maquette est un champ libre |
| Fichier .xlsx | **`documentId`** (UUID, réf. MS Document) | upload/stockage gérés par le microservice Document ; commit à la création |
| businessKey | **UUID** généré | ownerRef côté MS Document + businessKey Camunda (P3) |
| N° demande | `DC-AAAA-NNNN` généré | identifiant métier affiché |
| Statut + dates | enum + auditing JPA | cycle de vie de la maquette |

**Statuts** (cycle de la maquette) : `TRAITEMENT_SAB` → `RESULTATS_SAB` →
`EN_ATTENTE_VALIDATION` → `CLOTURE`. À la création, statut = `TRAITEMENT_SAB`.

## Endpoints (Phase 1)

### Demandes
- `POST /api/droits-communication` — créer une demande (201)
- `GET  /api/droits-communication/{id}` — détail d'une demande
- `GET  /api/droits-communication` — "Mes demandes" : liste **paginée + filtrable**
  (`?statut=&initiateur=&q=&page=&size=&sort=`)
- `GET  /api/droits-communication/stats` — compteurs par statut (onglets)

### Traitement SAB (Phase 2)
- `POST /api/droits-communication/{id}/traitement-sab` — lance le batch (parsing .xlsx +
  envoi SAB par lots) ; à la fin la demande passe en `RESULTATS_SAB`

### Flow de validation (Phase 3 — pilote Camunda en interne)
- `POST /api/droits-communication/{id}/clients/{clientId}/decision` — décision conforme/non conforme
- `POST /api/droits-communication/{id}/envoyer-validation` — `{ "valideur": "..." }`
- `POST /api/droits-communication/{id}/annuler-validation`
- `POST /api/droits-communication/{id}/valider`
- `POST /api/droits-communication/{id}/rejeter` — `{ "motif": "..." }`
- `GET  /api/droits-communication/{id}/rapport` — données du rapport (demande clôturée)
- `GET  /api/droits-communication/corbeille-validation?valideur=` — corbeille valideur

### Référentiels (alimentent les drop-downs)
- `GET /api/referentiels/organismes`
- `GET /api/referentiels/canaux-demande`
- `GET /api/referentiels/types-demande`

## Exemple de payload de création

Le front a déjà uploadé le `.xlsx` au **microservice Document** (draft), qui a renvoyé un
`documentId` (**UUID**) ; on ne reçoit que cet id (pas le binaire). À la création, on
génère un `businessKey` et on **commit** le document au MS Document (module `DC`,
ownerRef = `businessKey`), en miroir du pattern plateforme (`commitAttachments`).

```json
{
  "organisme": "BAM",
  "canalDemande": "COURRIER",
  "dateReception": "2026-06-20",
  "referenceDemande": "BAM/2026/1284",
  "adressePostale": "Avenue Mohammed V, Rabat",
  "complementAdresse": "Bâtiment A, 3e étage",
  "typesDemande": ["SOLDE", "RIB"],
  "objetDemande": "Communication des soldes et RIB",
  "documentId": "fc086e08-c42f-4916-b077-747a7ec75a75",
  "initiateur": "Salma EL HASSANI"
}
```

> Le client réel du MS Document (généré depuis son OpenAPI) remplacera le
> `DocumentAttachmentStub` ; voir `document/DocumentAttachmentPort`.

## Validation appliquée (alignée maquette)
- Obligatoires : organisme, canal, date de réception (**≤ aujourd'hui**),
  référence demande, au moins un type, `documentId`.
- Facultatifs : adresse postale, complément d'adresse (texte libre), objet (255 car. max).
- Le code `organisme` est vérifié contre la base (référentiel actif).
- Erreurs au format JSON homogène (`ApiError`).

## Lancement (dev, H2)
```bash
mvn spring-boot:run
# Console H2 : http://localhost:8080/h2-console (jdbc:h2:mem:droitsdb)
```

## Lancement (PostgreSQL)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```
