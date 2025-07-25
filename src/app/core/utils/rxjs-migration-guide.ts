// src/app/core/utils/rxjs-migration-guide.ts
// Guide de migration RxJS et correction des erreurs TypeScript courantes

import { Observable } from 'rxjs';
import { firstValueFrom, lastValueFrom } from 'rxjs';
import { HttpClient } from '@angular/common/http';

/**
 * Guide de bonnes pratiques RxJS pour QuickFlow
 * 
 * ❌ PROBLÈMES COURANTS ET SOLUTIONS ✅
 */

export class RxJSMigrationGuide {

  constructor(private http: HttpClient) {}

  /**
   * ❌ ERREUR: toPromise() est déprécié
   * 
   * Problème: 
   * - toPromise() peut retourner undefined
   * - Déprécié dans RxJS 7+
   * - Erreur TypeScript: "may be undefined"
   */
  
  // ❌ ANCIEN CODE (à éviter)
  async oldWayWithToPromise(): Promise<any[]> {
    // ❌ Ne pas faire ceci
    // return await this.http.get<any[]>('/api/data').toPromise() as Promise<any[]>;
    
    // Cette ligne génère l'erreur TypeScript TS2352
    throw new Error('Code exemple - ne pas utiliser');
  }

  /**
   * ✅ SOLUTION 1: firstValueFrom (recommandé)
   * 
   * Utiliser pour: Récupérer la première valeur émise
   * Cas d'usage: Requêtes HTTP GET simples
   */
  async newWayWithFirstValueFrom(): Promise<any[]> {
    // ✅ Correct - firstValueFrom garantit un résultat
    return await firstValueFrom(this.http.get<any[]>('/api/data'));
  }

  /**
   * ✅ SOLUTION 2: lastValueFrom  
   * 
   * Utiliser pour: Récupérer la dernière valeur émise
   * Cas d'usage: Observables qui émettent plusieurs valeurs
   */
  async newWayWithLastValueFrom(): Promise<any[]> {
    // ✅ Correct - lastValueFrom attend la completion
    return await lastValueFrom(this.http.get<any[]>('/api/data'));
  }

  /**
   * ✅ SOLUTION 3: Gestion d'erreur avec try/catch
   */
  async safeAsyncCall(): Promise<any[]> {
    try {
      return await firstValueFrom(this.http.get<any[]>('/api/data'));
    } catch (error) {
      console.error('Erreur lors de la requête:', error);
      throw error; // Ou retourner une valeur par défaut
    }
  }

  /**
   * ✅ SOLUTION 4: Avec valeur par défaut
   */
  async callWithDefault(): Promise<any[]> {
    try {
      return await firstValueFrom(this.http.get<any[]>('/api/data'));
    } catch (error) {
      console.warn('Utilisation des données par défaut:', error);
      return []; // Valeur par défaut
    }
  }

  /**
   * ✅ SOLUTION 5: Observable classique (pas de Promise)
   */
  getData(): Observable<any[]> {
    // ✅ Garder en Observable quand c'est approprié
    return this.http.get<any[]>('/api/data');
  }

  /**
   * ✅ SOLUTION 6: Conversion conditionnelle
   */
  async getDataConditional(usePromise: boolean): Promise<any[] | Observable<any[]>> {
    const request = this.http.get<any[]>('/api/data');
    
    if (usePromise) {
      return await firstValueFrom(request);
    } else {
      return request;
    }
  }
}

/**
 * 🔧 PATTERNS DE CORRECTION POUR LES SERVICES EXISTANTS
 */

export class ServiceMigrationPatterns {

  constructor(private http: HttpClient) {}

  /**
   * Pattern 1: Service CRUD basique
   */
  
  // ❌ Ancien pattern
  async oldGetData(): Promise<any[]> {
    // return this.http.get<any[]>('/api/data').toPromise() as Promise<any[]>;
    throw new Error('Ancien pattern - à migrer');
  }

  // ✅ Nouveau pattern
  async newGetData(): Promise<any[]> {
    return firstValueFrom(this.http.get<any[]>('/api/data'));
  }

  /**
   * Pattern 2: POST avec réponse
   */
  
  // ✅ Création avec firstValueFrom
  async createItem(item: any): Promise<any> {
    return firstValueFrom(
      this.http.post<any>('/api/items', item)
    );
  }

  /**
   * Pattern 3: PUT avec gestion d'erreur
   */
  async updateItem(id: string, item: any): Promise<any> {
    try {
      return await firstValueFrom(
        this.http.put<any>(`/api/items/${id}`, item)
      );
    } catch (error) {
      console.error(`Erreur mise à jour item ${id}:`, error);
      throw new Error(`Impossible de mettre à jour l'item ${id}`);
    }
  }

  /**
   * Pattern 4: DELETE avec confirmation
   */
  async deleteItem(id: string): Promise<boolean> {
    try {
      await firstValueFrom(
        this.http.delete(`/api/items/${id}`)
      );
      return true;
    } catch (error) {
      console.error(`Erreur suppression item ${id}:`, error);
      return false;
    }
  }

  /**
   * Pattern 5: Recherche avec paramètres
   */
  async searchItems(query: string): Promise<any[]> {
    const params = { q: query };
    return firstValueFrom(
      this.http.get<any[]>('/api/items/search', { params })
    );
  }
}

/**
 * 📋 CHECKLIST DE MIGRATION
 * 
 * Pour migrer un service existant:
 * 
 * 1. ✅ Ajouter l'import: 
 *    import { firstValueFrom } from 'rxjs';
 * 
 * 2. ✅ Remplacer .toPromise() par firstValueFrom():
 *    - Avant: .toPromise() as Promise<T>
 *    - Après: firstValueFrom(observable)
 * 
 * 3. ✅ Vérifier le typage:
 *    - firstValueFrom<T>() retourne Promise<T>
 *    - Pas besoin de cast manuel
 * 
 * 4. ✅ Gérer les erreurs:
 *    - try/catch autour de await firstValueFrom()
 *    - Ou .catch() sur la Promise
 * 
 * 5. ✅ Tester:
 *    - Vérifier que les appels fonctionnent
 *    - Vérifier la gestion d'erreur
 */

/**
 * 🚨 ERREURS TYPESCRIPT COMMUNES ET SOLUTIONS
 */

export interface TypeScriptErrorSolutions {
  
  /**
   * ERREUR: TS2352 - Conversion may be a mistake
   * 
   * Cause: toPromise() peut retourner undefined
   * Solution: Utiliser firstValueFrom() ou lastValueFrom()
   */
  TS2352_ConversionError: {
    problem: 'toPromise() peut retourner undefined';
    solution: 'Utiliser firstValueFrom() qui garantit une valeur';
    example: 'firstValueFrom(this.http.get<T>(url))';
  };

  /**
   * ERREUR: TS2322 - Type 'Observable<T>' is not assignable to type 'Promise<T>'
   * 
   * Cause: Tentative d'assigner un Observable à une Promise
   * Solution: Convertir explicitement avec firstValueFrom()
   */
  TS2322_AssignmentError: {
    problem: 'Observable assigné à Promise sans conversion';
    solution: 'Convertir avec firstValueFrom() ou changer le type de retour';
    example: 'return firstValueFrom(observable) ou retourner Observable<T>';
  };

  /**
   * ERREUR: TS2345 - Argument of type 'Observable<T>' is not assignable
   * 
   * Cause: Passage d'Observable là où Promise est attendue
   * Solution: Convertir avant passage
   */
  TS2345_ArgumentError: {
    problem: 'Observable passé où Promise attendue';
    solution: 'Convertir avant de passer en paramètre';
    example: 'method(await firstValueFrom(observable))';
  };
}

/**
 * 🔍 UTILITAIRES DE DEBUG
 */

export class RxJSDebugUtils {

  /**
   * Vérifier si une valeur est une Promise
   */
  static isPromise(value: any): value is Promise<any> {
    return value && typeof value.then === 'function';
  }

  /**
   * Vérifier si une valeur est un Observable
   */
  static isObservable(value: any): value is Observable<any> {
    return value && typeof value.subscribe === 'function';
  }

  /**
   * Convertir intelligemment Observable vers Promise
   */
  static async toPromise<T>(source: Observable<T> | Promise<T>): Promise<T> {
    if (this.isPromise(source)) {
      return source;
    }
    if (this.isObservable(source)) {
      return firstValueFrom(source);
    }
    throw new Error('Source doit être Observable ou Promise');
  }

  /**
   * Log le type d'une valeur pour debugging
   */
  static logType(value: any, label: string = 'Value'): void {
    console.log(`${label} type:`, {
      isPromise: this.isPromise(value),
      isObservable: this.isObservable(value),
      constructor: value?.constructor?.name,
      value: value
    });
  }
}

/**
 * EXEMPLE D'UTILISATION:
 * 
 * // Dans votre service
 * import { firstValueFrom } from 'rxjs';
 * 
 * // ✅ Correct
 * async getData(): Promise<MyData[]> {
 *   return firstValueFrom(this.http.get<MyData[]>('/api/data'));
 * }
 * 
 * // ✅ Avec gestion d'erreur
 * async getDataSafe(): Promise<MyData[]> {
 *   try {
 *     return await firstValueFrom(this.http.get<MyData[]>('/api/data'));
 *   } catch (error) {
 *     console.error('Erreur:', error);
 *     return [];
 *   }
 * }
 */
