import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { 
  DerogationFormData, 
  BUSINESS_LINES, 
  SEGMENTS, 
  TYPES_DEROGATION, 
  DEVISES 
} from '../../models/form/derogation-form.interface';
import { DerogationFormService } from '../../services/form/ derogation-form.service';

@Component({
  selector: 'app-derogation-form',
  templateUrl: './derogation-form.component.html',
  styleUrls: ['./derogation-form.component.scss']
})
export class DerogationFormComponent implements OnInit {

  derogationForm!: FormGroup;
  uploadedFiles: File[] = [];
  isIdSabValidated = false;
  isValidatingIdSab = false;
  isSubmitting = false;
  isSavingDraft = false;

  // Options pour les dropdowns
  businessLines = BUSINESS_LINES;
  segments = SEGMENTS;
  typesDerogation = TYPES_DEROGATION;
  devises = DEVISES;

  constructor(
    private fb: FormBuilder,
    private derogationFormService: DerogationFormService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.derogationForm = this.fb.group({
      idSab: ['', [Validators.required, Validators.minLength(6)]],
      nombreComptes: [null],
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      businessLine: ['', Validators.required],
      segment: ['', Validators.required],
      typeDerogation: ['', Validators.required],
      codeCommercial: [''],
      justificationStandard: [''],
      devise: ['', Validators.required],
      dateEffet: ['', Validators.required],
      dateFin: ['', Validators.required],
      tauxMontantDerogation: ['', Validators.required],
      tiers: [''],
      convention: [''],
      employeur: [''],
      salaire: [null],
      fnb: [null],
      commentaire: ['']
    });
  }

  // Validation ID SAB
  onValidateIdSab(): void {
    const idSab = this.derogationForm.get('idSab')?.value;
    if (!idSab || idSab.length < 6) return;

    this.isValidatingIdSab = true;
    
    this.derogationFormService.validateIdSabMock(idSab).subscribe({
      next: (response) => {
        if (response.isValid && response.clientInfo) {
          this.isIdSabValidated = true;
          this.derogationForm.patchValue({
            nom: response.clientInfo.nom,
            prenom: response.clientInfo.prenom,
            nombreComptes: response.clientInfo.nombreComptes
          });
        }
        this.isValidatingIdSab = false;
      },
      error: () => {
        this.isValidatingIdSab = false;
      }
    });
  }

  // Upload de documents
  onFileSelected(event: any): void {
    const files: FileList = event.target.files;
    for (let i = 0; i < files.length; i++) {
      this.uploadedFiles.push(files[i]);
    }
  }

  removeFile(index: number): void {
    this.uploadedFiles.splice(index, 1);
  }

  // Actions des boutons
  onReturn(): void {
    this.router.navigate(['/derogation']);
  }

  onSaveAndExit(): void {
    if (this.derogationForm.invalid) return;

    this.isSavingDraft = true;
    const formData: DerogationFormData = {
      ...this.derogationForm.value,
      documents: this.uploadedFiles
    };

    this.derogationFormService.saveDraft(formData).subscribe({
      next: (response) => {
        console.log('Brouillon sauvegardé:', response.id);
        this.isSavingDraft = false;
        this.router.navigate(['/derogation']);
      },
      error: () => {
        this.isSavingDraft = false;
      }
    });
  }

  onSubmit(): void {
    if (this.derogationForm.invalid || !this.isIdSabValidated) {
      this.markFormGroupTouched();
      return;
    }

    this.isSubmitting = true;
    const formData: DerogationFormData = {
      ...this.derogationForm.value,
      documents: this.uploadedFiles
    };

    this.derogationFormService.submitRequest(formData).subscribe({
      next: (response) => {
        console.log('Demande soumise:', response.requestId);
        this.isSubmitting = false;
        this.router.navigate(['/derogation'], { 
          queryParams: { success: 'true', requestId: response.requestId } 
        });
      },
      error: () => {
        this.isSubmitting = false;
      }
    });
  }

  private markFormGroupTouched(): void {
    Object.keys(this.derogationForm.controls).forEach(key => {
      this.derogationForm.get(key)?.markAsTouched();
    });
  }

  // Helpers pour le template
  isFieldInvalid(fieldName: string): boolean {
    const field = this.derogationForm.get(fieldName);
    return !!(field?.invalid && field?.touched);
  }

  getFieldError(fieldName: string): string {
    const field = this.derogationForm.get(fieldName);
    if (field?.errors?.['required']) return 'Ce champ est requis';
    if (field?.errors?.['minlength']) return 'Longueur minimale non respectée';
    return '';
  }
}