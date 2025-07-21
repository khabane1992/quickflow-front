import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { DerogationFormData } from '../../models/form/derogation-form.interface';

@Injectable({
  providedIn: 'root'
})
export class DerogationFormService {

  private apiUrl = '/api/derogation';

  constructor(private http: HttpClient) {}

  // Valider l'ID SAB
  validateIdSab(idSab: string): Observable<{isValid: boolean, clientInfo?: any}> {
    return this.http.post<{isValid: boolean, clientInfo?: any}>(`${this.apiUrl}/validate-sab`, { idSab });
  }

  // Sauvegarder le brouillon
  saveDraft(formData: DerogationFormData): Observable<{success: boolean, id: string}> {
    return this.http.post<{success: boolean, id: string}>(`${this.apiUrl}/save-draft`, formData);
  }

  // Soumettre la demande
  submitRequest(formData: DerogationFormData): Observable<{success: boolean, requestId: string}> {
    const formDataToSend = new FormData();
    
    // Ajouter les données du formulaire
    Object.keys(formData).forEach(key => {
      if (key !== 'documents') {
        formDataToSend.append(key, formData[key as keyof DerogationFormData] as string);
      }
    });
    
    // Ajouter les fichiers
    formData.documents.forEach((file, index) => {
      formDataToSend.append(`documents[${index}]`, file);
    });
    
    return this.http.post<{success: boolean, requestId: string}>(`${this.apiUrl}/submit`, formDataToSend);
  }

  // Upload de document
  uploadDocument(file: File): Observable<{success: boolean, fileId: string}> {
    const formData = new FormData();
    formData.append('document', file);
    return this.http.post<{success: boolean, fileId: string}>(`${this.apiUrl}/upload-document`, formData);
  }

  // Mock pour validation ID SAB
  validateIdSabMock(idSab: string): Observable<{isValid: boolean, clientInfo?: any}> {
    const mockResponse = {
      isValid: idSab.length >= 6,
      clientInfo: idSab.length >= 6 ? {
        nom: 'Dupont',
        prenom: 'Jean',
        nombreComptes: 2
      } : null
    };
    return of(mockResponse);
  }
}