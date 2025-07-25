# Guide d'utilisation du Mock Server - QuickFlow

## Vue d'ensemble

Ce système de Mock Server intégré permet de développer l'application QuickFlow sans dépendre du backend. Il simule toutes les API nécessaires avec des données réalistes et offre une transition fluide vers le vrai backend.

## 🚀 Démarrage rapide

### 1. Configuration initiale

Le mock server est **activé par défaut** en mode développement. Pour le vérifier :

```typescript
// src/environments/environment.ts
export const environment = {
  useMockApi: true,  // ← true = Mock activé, false = API réelle
  apiUrl: 'http://localhost:8080/api'
};
```

### 2. Lancement de l'application

```bash
npm start
# ou
ng serve
```

L'application démarre avec le mock server actif. Une notification vous l'indiquera.

## 🎛️ Panneau de contrôle (Debug Panel)

Un panneau de debug apparaît automatiquement en mode développement (coin supérieur droit).

### Fonctionnalités du panneau :

- **Mode API** : Affiche le mode actuel (Mock/Real)
- **Toggle** : Bascule entre Mock et Real API
- **Check Backend** : Teste la disponibilité du vrai backend
- **Auto Switch** : Bascule automatiquement vers Mock si le backend est indisponible
- **Test Notifications** : Teste le système de notifications
- **Configuration détaillée** : Affiche tous les paramètres

## 📊 Données mockées disponibles

### Types de dérogations
- **TARIFF** : Dérogation tarifaire
- **LIMIT** : Dérogation de limite  
- **COMMISSION** : Dérogation de commission
- **PROCEDURE** : Dérogation procédurale
- **OTHER** : Autre dérogation

### Statuts simulés
- **DRAFT** : Brouillon
- **PENDING** : En attente
- **IN_REVIEW** : En cours de traitement
- **APPROVED** : Approuvée
- **REJECTED** : Rejetée
- **IMPLEMENTED** : Implémentée
- **CANCELLED** : Annulée

### Données d'exemple
- 8 demandes de dérogation avec différents statuts
- Propriétaires : Anna Aiello, Marc Dupont, Sophie Martin, etc.
- Agences : Paris, Lyon, Marseille, Toulouse, etc.
- Montants : de 5 000€ à 30 000€

## 🔧 Endpoints mockés

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/api/dashboard/demands-to-process` | GET | Demandes à traiter |
| `/api/dashboard/demands-pending-validation` | GET | Demandes en attente |
| `/api/dashboard/demands-processed` | GET | Demandes traitées |
| `/api/dashboard/demands/{id}` | GET | Détail d'une demande |
| `/api/dashboard/demands/{id}/status` | PUT | Mise à jour du statut |
| `/api/dashboard/demands/search` | GET | Recherche dans les demandes |
| `/api/dashboard/stats` | GET | Statistiques du dashboard |
| `/api/demands` | GET/POST | CRUD des demandes |
| `/api/demands/{id}` | DELETE | Suppression d'une demande |

## 🔄 Basculer entre Mock et Real API

### Méthode 1 : Via le Debug Panel
Cliquez sur les boutons "Mock API" ou "Real API" dans le panneau.

### Méthode 2 : Via le code
```typescript
// Dans un composant
constructor(private apiConfig: ApiConfigService) {}

// Activer le mock
this.apiConfig.enableMockApi();

// Désactiver le mock  
this.apiConfig.disableMockApi();

// Basculer
this.apiConfig.toggleMockApi();
```

### Méthode 3 : Via l'environnement
```typescript
// src/environments/environment.ts
export const environment = {
  useMockApi: false  // ← Changer ici
};
```

## 📡 Simulation réseau

### Délais simulés
```typescript
mockApiDelays: {
  short: 200,    // Opérations rapides
  medium: 400,   // Opérations moyennes  
  long: 800      // Opérations lentes
}
```

### Gestion d'erreurs
- Taux d'erreur configurable : `mockErrorRate: 0` (0-100%)
- Simulation d'erreurs HTTP (400, 401, 403, 404, 500)
- Messages d'erreur réalistes

## 🛠️ Personnalisation du Mock

### Ajouter des données
```typescript
// Dans MockApiService
addMockData(newData: DashboardRequestDTO[]): void {
  this.mockData.push(...newData);
}
```

### Modifier les délais
```typescript
// Dans environment.ts
mockApiDelays: {
  short: 100,    // Plus rapide
  medium: 300,   
  long: 600      
}
```

### Simuler des erreurs
```typescript
// Dans environment.ts
mockErrorRate: 10  // 10% d'erreurs simulées
```

## 🔍 Debugging et logs

### Activation des logs
```typescript
// Dans environment.ts
enableMockLogs: true
```

### Logs disponibles
- Requêtes interceptées
- Données retournées
- Changements de mode API
- Erreurs simulées

### Console logs
```javascript
// Vérifier le mode actuel
console.log('Mode API:', apiConfig.isMockEnabled() ? 'MOCK' : 'REAL');

// Logs automatiques avec [MOCK API] prefix
```

## 🚀 Transition vers le vrai backend

### 1. Préparation
```bash
# S'assurer que le backend est prêt
curl http://localhost:8080/api/health
```

### 2. Configuration
```typescript
// src/environments/environment.ts
export const environment = {
  useMockApi: false,  // ← Désactiver le mock
  apiUrl: 'http://localhost:8080/api'  // ← URL du vrai backend
};
```

### 3. Test graduel
```typescript
// Basculer temporairement pour tester
this.apiConfig.disableMockApi();

// Tester une fonction
this.derogationService.getDemandsToProcess('user123').subscribe(
  data => console.log('Backend fonctionne:', data),
  error => {
    console.error('Backend indisponible:', error);
    this.apiConfig.enableMockApi(); // Revenir au mock
  }
);
```

### 4. Auto-switch
```typescript
// Basculement automatique si backend indisponible
await this.apiConfig.autoSwitchToMockIfNeeded();
```

## 📱 Notifications utilisateur

Le système informe automatiquement l'utilisateur :

- **Mode Mock activé** : Notification d'avertissement orange
- **Mode Real activé** : Notification de succès verte  
- **Backend indisponible** : Notification d'erreur avec option de basculement
- **Basculement automatique** : Notification d'information bleue

## 🧪 Tests et développement

### Scenarios de test
```typescript
// Test des notifications
this.notificationService.showMockApiEnabled();
this.notificationService.showBackendUnavailable(() => {
  this.apiConfig.enableMockApi();
});

// Test des endpoints
this.mockApiService.getDemandsToProcess('test-user').subscribe(data => {
  console.log('Mock data:', data);
});

// Test des erreurs
this.mockApiService.simulateError().subscribe(
  () => {},
  error => console.log('Erreur simulée:', error)
);
```

### Développement multi-équipes
- **Frontend** : Travaille avec le mock
- **Backend** : Développe les vraies API  
- **Intégration** : Bascule facile entre les deux

## 🔒 Production

### Configuration production
```typescript
// src/environments/environment.prod.ts
export const environment = {
  production: true,
  useMockApi: false,  // ← TOUJOURS false en production
  apiUrl: 'https://api.quickflow.com'
};
```

### Sécurité
- Mock automatiquement désactivé en production
- Debug panel masqué en production
- Logs de mock désactivés

## ❓ FAQ

### Q: Comment savoir si le mock est actif ?
**R:** Vérifiez la notification au démarrage ou le debug panel.

### Q: Les données sont-elles persistantes ?
**R:** Non, les données mock sont en mémoire et se réinitialisent à chaque rechargement.

### Q: Comment ajouter de nouveaux endpoints ?
**R:** Modifiez `MockApiInterceptor` et `MockApiService`.

### Q: Le mock fonctionne-t-il avec tous les services ?
**R:** Oui, il intercepte toutes les requêtes HTTP vers les endpoints configurés.

### Q: Comment tester les erreurs ?
**R:** Augmentez `mockErrorRate` ou utilisez `simulateError()`.

## 🤝 Contribution

Pour étendre le mock server :

1. **Nouveaux endpoints** : Ajoutez dans `MockApiInterceptor.handleDashboardEndpoints()`
2. **Nouvelles données** : Étendez `MockApiService.mockData`
3. **Nouveaux services** : Suivez le pattern de `DerogationManagementService`

## 📞 Support

- Vérifiez les logs avec `enableMockLogs: true`
- Utilisez le debug panel pour diagnostiquer
- Consultez la configuration avec "Copy Config"
- Testez la connectivité backend avec "Check Backend"

---

**Note**: Ce mock server est conçu pour faciliter le développement. Assurez-vous de le désactiver en production !
