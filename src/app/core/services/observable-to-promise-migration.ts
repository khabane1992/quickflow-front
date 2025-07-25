// src/app/core/services/observable-to-promise-migration.ts
// Guide de migration de toPromise() vers les nouvelles méthodes RxJS

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, firstValueFrom, lastValueFrom, take } from 'rxjs';

/**
 * 🚨 PROBLÈME AVEC toPromise()
 * 
 * toPromise() est DEPRECATED depuis RxJS 7+ et cause des erreurs TypeScript :
 * "Conversion of type any[] | undefined to type Promise<any[]> may be a mistake"
 * 
 * Cela arrive car toPromise() peut retourner undefined si l'Observable se complete
 * sans émettre de valeur.
 */

@Injectable({
  providedIn: 'root'
})
export class ObservableToPromiseMigrationService {

  constructor(private http: HttpClient) {}

  /**
   * ❌ ANCIENNE MÉTHODE (DEPRECATED)
   * Cause l'erreur TypeScript TS2352
   */
  async getDataOldWay(): Promise<any[]> {
    // ❌ Cette ligne cause l'erreur
    // return await this.http.get<any[]>('/api/data').toPromise() as Promise<any[]>;
    
    // Le casting "as Promise<any[]>" est nécessaire mais dangereux
    // car toPromise() peut retourner undefined
    return []; // Placeholder pour éviter l'erreur
  }

  /**
   * ✅ NOUVELLE MÉTHODE 1: firstValueFrom()
   * 
   * firstValueFrom() prend la PREMIÈRE valeur émise par l'Observable
   * et se complete immédiatement après.
   * 
   * Utiliser quand: Vous voulez juste la première réponse (cas le plus courant pour HTTP)
   */
  async getDataWithFirstValue(): Promise<any[]> {
    try {
      // ✅ Approche recommandée
      const data = await firstValueFrom(this.http.get<any[]>('/api/data'));
      return data;
    } catch (error) {
      console.error('Erreur lors de la récupération:', error);
      throw error;
    }
  }

  /**
   * ✅ NOUVELLE MÉTHODE 2: lastValueFrom()
   * 
   * lastValueFrom() attend que l'Observable se complete et prend la DERNIÈRE valeur émise.
   * 
   * Utiliser quand: Vous voulez attendre que l'Observable se complete entièrement
   * (rare pour les requêtes HTTP simples)
   */
  async getDataWithLastValue(): Promise<any[]> {
    try {
      // Pour les requêtes HTTP simples, firstValueFrom() est généralement préférable
      const data = await lastValueFrom(this.http.get<any[]>('/api/data'));
      return data;
    } catch (error) {
      console.error('Erreur lors de la récupération:', error);
      throw error;
    }
  }

  /**
   * ✅ NOUVELLE MÉTHODE 3: avec take(1)
   * 
   * Utilise take(1) pour s'assurer qu'on ne prend qu'une seule valeur
   * puis firstValueFrom() pour la convertir en Promise.
   */
  async getDataWithTake(): Promise<any[]> {
    try {
      const data = await firstValueFrom(
        this.http.get<any[]>('/api/data').pipe(take(1))
      );
      return data;
    } catch (error) {
      console.error('Erreur lors de la récupération:', error);
      throw error;
    }
  }

  /**
   * ✅ MÉTHODE 4: Gestion des cas d'erreur avec valeur par défaut
   */
  async getDataWithDefault(): Promise<any[]> {
    try {
      return await firstValueFrom(this.http.get<any[]>('/api/data'));
    } catch (error) {
      console.warn('Impossible de récupérer les données, utilisation des données par défaut');
      // Retourner des données par défaut en cas d'erreur
      return [];
    }
  }

  /**
   * ✅ MÉTHODE 5: Avec timeout et gestion d'erreur avancée
   */
  async getDataWithTimeout(): Promise<any[]> {
    try {
      return await firstValueFrom(
        this.http.get<any[]>('/api/data')
        // Vous pouvez ajouter des opérateurs RxJS ici si nécessaire
        // .pipe(timeout(5000), retry(2))
      );
    } catch (error) {
      if (error.name === 'TimeoutError') {
        throw new Error('Timeout: La requête a pris trop de temps');
      }
      throw error;
    }
  }

  /**
   * ✅ COMPARAISON DES APPROCHES
   */
  
  // Pour les requêtes HTTP simples (GET, POST, PUT, DELETE)
  async httpRequest(): Promise<any> {
    // ✅ RECOMMANDÉ
    return await firstValueFrom(this.http.get('/api/endpoint'));
  }

  // Pour les Observables qui émettent plusieurs valeurs
  async multipleValues(): Promise<any> {
    // Si vous voulez la première valeur
    return await firstValueFrom(this.createObservable());
    
    // Si vous voulez la dernière valeur (après completion)
    // return await lastValueFrom(this.createObservable());
  }

  // Observable d'exemple qui émet plusieurs valeurs
  private createObservable(): Observable<number> {
    return new Observable(observer => {
      observer.next(1);
      observer.next(2);
      observer.next(3);
      observer.complete();
    });
  }

  /**
   * 🔧 RÈGLES DE MIGRATION
   */
  
  // ❌ Remplacer ceci:
  // return await observable.toPromise() as Promise<T>;
  
  // ✅ Par ceci:
  // return await firstValueFrom(observable);

  /**
   * 📋 CHECKLIST DE MIGRATION
   * 
   * 1. ✅ Importer firstValueFrom/lastValueFrom depuis 'rxjs'
   * 2. ✅ Remplacer .toPromise() par firstValueFrom()
   * 3. ✅ Supprimer les castings "as Promise<T>"
   * 4. ✅ Tester que les erreurs sont bien gérées
   * 5. ✅ Vérifier que le comportement est identique
   */

  /**
   * 🚨 CAS PARTICULIERS
   */

  // Si l'Observable peut ne pas émettre de valeur
  async getDataMaybeEmpty(): Promise<any[] | undefined> {
    try {
      return await firstValueFrom(this.http.get<any[]>('/api/data'));
    } catch (error) {
      // firstValueFrom() lance une erreur si l'Observable se complete sans émettre
      console.warn('Aucune donnée émise');
      return undefined;
    }
  }

  // Si vous devez absolument utiliser toPromise() (non recommandé)
  async getDataWithToPromiseWorkaround(): Promise<any[]> {
    const result = await this.http.get<any[]>('/api/data').toPromise();
    
    // Gestion explicite du cas undefined
    if (result === undefined) {
      throw new Error('Aucune donnée reçue');
    }
    
    return result;
  }
}

/**
 * 📚 RÉSUMÉ DES BONNES PRATIQUES
 * 
 * ✅ DO:
 * - Utiliser firstValueFrom() pour les requêtes HTTP
 * - Utiliser lastValueFrom() pour les Observables qui se complètent
 * - Gérer les erreurs avec try/catch
 * - Tester le comportement après migration
 * 
 * ❌ DON'T:
 * - Utiliser toPromise() (deprecated)
 * - Ignorer les cas d'erreur
 * - Utiliser des castings dangereux "as Promise<T>"
 * - Oublier d'importer firstValueFrom/lastValueFrom
 */
