import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-derogation-request',
  templateUrl: './derogation-request.component.html',
  styleUrls: ['./derogation-request.component.scss']
})
export class DerogationRequestComponent implements OnInit {
  derogationForm: FormGroup;
  uploadedFiles: File[] = [];

  businessLineOptions = [
    'Option 1',
    'Option 2',
    'Option 3'
  ];

  segmentOptions = [
    'Segment A',
    'Segment B',
    'Segment C'
  ];

  derogationTypeOptions = [
    'Type 1',
    'Type 2',
    'Type 3'
  ];

  deviseOptions = [
    'EUR',
    'USD',
    'GBP'
  ];

  constructor(private fb: FormBuilder) {
    this.derogationForm = this.fb.group({
      idSab: ['', Validators.required],
      numeroCompte: ['', Validators.required],
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      businessLine: ['', Validators.required],
      segment: ['', Validators.required],
      typeDerogation: ['', Validators.required],
      codeCommission: [''],
      tarificationStandard: [''],
      devise: [''],
      dateEffet: ['', Validators.required],
      dateFin: ['', Validators.required],
      tauxMontant: [''],
      taux: [''],
      convention: [''],
      employeur: [''],
      salaire: [''],
      pnb: [''],
      commentaire: ['']
    });
  }

  ngOnInit(): void {}

  onConfirmId() {
    // Logic to confirm SAB ID
    console.log('Confirming SAB ID:', this.derogationForm.get('idSab')?.value);
  }

  onFileSelected(event: any) {
    const files = event.target.files;
    if (files) {
      for (let file of files) {
        this.uploadedFiles.push(file);
      }
    }
  }

  removeFile(index: number) {
    this.uploadedFiles.splice(index, 1);
  }

  onSubmit() {
    if (this.derogationForm.valid) {
      console.log('Form submitted:', this.derogationForm.value);
      // Submit logic here
    } else {
      console.log('Form is invalid');
      this.markFormGroupTouched();
    }
  }

  onSaveAndExit() {
    console.log('Save and exit:', this.derogationForm.value);
    // Save draft logic here
  }

  onReturn() {
    // Navigation logic back to previous page
    window.history.back();
  }

  private markFormGroupTouched() {
    Object.keys(this.derogationForm.controls).forEach(key => {
      const control = this.derogationForm.get(key);
      control?.markAsTouched();
    });
  }
}
