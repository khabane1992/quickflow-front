export const environment = {
  production: true,
  apiUrl: 'https://your-production-api.com/api', // ← URL de votre API de production
  
  // Configuration Mock API - DÉSACTIVÉ en production
  useMockApi: false,
  
  // Configuration des délais de simulation (non utilisés en production)
  mockApiDelays: {
    short: 0,
    medium: 0,
    long: 0
  },
  
  // Configuration des erreurs simulées (désactivée en production)
  mockErrorRate: 0,
  
  // Configuration du logging (désactivé en production)
  enableMockLogs: false,
  
  // Fonctionnalités activées
  features: {
    dashboard: true,
    notifications: true,
    advancedSearch: true,
    fileUpload: true,
    exportData: true
  }
};
