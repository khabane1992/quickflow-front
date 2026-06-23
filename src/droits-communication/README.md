# Microservice — Droits de Communication

Backend Spring Boot du formulaire **« Nouvelle demande »** (user story Initiateur).
Stack : Java 17, Spring Boot 3.2.5, Spring Data JPA, MapStruct, Lombok, PostgreSQL (H2 en dev).

## Architecture (structure Facade)

```
controller  → expose l'API REST (validation @Valid)
facade      → orchestre mapping + service
service     → logique métier + validation des référentiels
repository  → accès données (JPA)
entity      → modèle persisté
dto         → Request / Response / ReferentielItem
mapper      → MapStruct entité <-> DTO
referentiel → Organisme & ComplementAdresse (tables) + ReferentielService
enums       → CanalDemande, TypeDemande, StatutDemande
exception   → handler global + erreurs métier
config      → auditing JPA + seed référentiels
```

## Choix de modélisation

| Élément | Choix | Raison |
|---|---|---|
| Canal de la demande | enum `CanalDemande` | référentiel stable |
| Type de demande | `Set<TypeDemande>` (enum) | la maquette est multi-sélection |
| Organisme | table `ref_organisme` | référentiel évolutif |
| Complément d'adresse | table `ref_complement_adresse` | référentiel évolutif, facultatif |
| Fichier joint | référence + métadonnées | l'upload est géré par un autre microservice |
| Statut + dates | enum + auditing JPA | suivi du cycle de vie |

## Endpoints

### Demandes
- `POST /api/droits-communication` — créer une demande (201)
- `GET  /api/droits-communication/{id}` — consulter une demande
- `GET  /api/droits-communication` — lister les demandes

### Référentiels (alimentent les drop-downs)
- `GET /api/referentiels/organismes`
- `GET /api/referentiels/complements-adresse`
- `GET /api/referentiels/canaux-demande`
- `GET /api/referentiels/types-demande`

## Exemple de payload de création

```json
{
  "organisme": "DGFIP",
  "adressePostale": "12 rue de la Paix, 75002 Paris",
  "complementAdresse": "BAT",
  "objetDemande": "Communication des relevés de compte sur 12 mois",
  "canalDemande": "COURRIER",
  "referenceDemande": "REF-2026-00123",
  "dateReception": "2026-06-20",
  "typesDemande": ["SOLDE", "RIB"],
  "documentReference": "doc-uuid-renvoyé-par-le-ms-upload",
  "documentNom": "liste_droits.pdf",
  "documentContentType": "application/pdf"
}
```

## Validation appliquée
- Champs obligatoires : organisme, adresse postale, objet, canal, date de réception, au moins un type.
- `complementAdresse` facultatif (cohérent avec la user story).
- Les codes `organisme` / `complementAdresse` sont vérifiés contre la base (référentiel actif).
- Erreurs renvoyées au format JSON homogène (`ApiError`).

## Lancement (dev, H2)
```bash
mvn spring-boot:run
# Console H2 : http://localhost:8080/h2-console (jdbc:h2:mem:droitsdb)
```

## Lancement (PostgreSQL)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

## Correctifs apportés vs le code initial
- Un seul `maven-compiler-plugin` (les deux versions dupliquées causaient un conflit).
- Ajout de `lombok-mapstruct-binding` : sans lui, MapStruct ignore les accesseurs Lombok et génère des mappings vides.
- `canalDemande` / `typeDemande` passés de `String` à enums.
- `typeDemande` passé en `Set` pour gérer la multi-sélection de la maquette.
- Ajout statut, auditing, gestion d'erreurs, validation, et endpoints référentiels.
