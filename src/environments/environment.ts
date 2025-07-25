export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api', // ← URL de votre vrai backend

  // Configuration Mock API
  useMockApi: true, // ← Basculer vers false quand le backend est prêt

  // Configuration des délais de simulation (en ms)
  mockApiDelays: {
    short: 200,    // Pour les opérations rapides
    medium: 400,   // Pour les opérations moyennes
    long: 800      // Pour les opérations lentes
  },

  // Configuration des erreurs simulées
  mockErrorRate: 0, // 0-100 (pourcentage d'erreurs simulées)

  // Configuration du logging
  enableMockLogs: true,

  // Fonctionnalités activées
  features: {
    dashboard: true,
    notifications: true,
    advancedSearch: true,
    fileUpload: true,
    exportData: true
  }
};
