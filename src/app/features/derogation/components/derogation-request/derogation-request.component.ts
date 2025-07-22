import {Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-derogation-request',
  templateUrl: './derogation-request.component.html',
  styleUrls: ['./derogation-request.component.scss']
})
export class DerogationRequestComponent implements OnInit {
  derogationForm: FormGroup;
  uploadedFiles: File[] = [];

  @ViewChild('richTextEditor', {static: false}) richTextEditor!: ElementRef;

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
      commentaire: [''] // This will contain rich text with markdown-style formatting
    });
  }

  ngOnInit(): void {
  }

  // Rich Text Editor Methods - Add formatting markers
  execCommand(command: string, value?: string) {
    if (!this.richTextEditor) return;

    const textarea = this.richTextEditor.nativeElement;
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const selectedText = textarea.value.substring(start, end);

    if (selectedText) {
      let wrappedText = '';

      switch (command) {
        case 'bold':
          wrappedText = `**${selectedText}**`;
          break;
        case 'italic':
          wrappedText = `*${selectedText}*`;
          break;
        case 'underline':
          wrappedText = `_${selectedText}_`;
          break;
        case 'removeFormat':
          // Remove all formatting markers
          wrappedText = selectedText
            .replace(/\*\*(.*?)\*\*/g, '$1')  // Remove bold
            .replace(/\*(.*?)\*/g, '$1')      // Remove italic
            .replace(/_(.*?)_/g, '$1')        // Remove underline
            .replace(/\[.*?\](.*?)\[\/.*?\]/g, '$1'); // Remove color/size markers
          break;
        default:
          wrappedText = selectedText;
      }

      const newValue = textarea.value.substring(0, start) + wrappedText + textarea.value.substring(end);

      // Update the form control value
      this.derogationForm.patchValue({commentaire: newValue});

      // Restore cursor position
      setTimeout(() => {
        const newCursorPos = start + wrappedText.length;
        textarea.setSelectionRange(newCursorPos, newCursorPos);
        textarea.focus();
      });
    } else {
      textarea.focus();
    }
  }

  // Color picker - Add color markers around selected text
  changeColor(color: string) {
    if (!this.richTextEditor) return;

    const textarea = this.richTextEditor.nativeElement;
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const selectedText = textarea.value.substring(start, end);

    if (selectedText) {
      const colorName = this.getColorName(color);
      const wrappedText = `[${colorName}]${selectedText}[/${colorName}]`;

      const newValue = textarea.value.substring(0, start) + wrappedText + textarea.value.substring(end);

      // Update the form control value
      this.derogationForm.patchValue({commentaire: newValue});

      setTimeout(() => {
        const newCursorPos = start + wrappedText.length;
        textarea.setSelectionRange(newCursorPos, newCursorPos);
        textarea.focus();
      });
    } else {
      textarea.focus();
    }
  }

  getColorName(color: string): string {
    switch (color) {
      case '#000000':
        return 'noir';
      case '#ff0000':
        return 'rouge';
      case '#4CAF50':
        return 'vert';
      case '#2196F3':
        return 'bleu';
      default:
        return 'couleur';
    }
  }

  // Font size - Add size markers around selected text
  changeFontSize(size: string) {
    if (!this.richTextEditor) return;

    const textarea = this.richTextEditor.nativeElement;
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const selectedText = textarea.value.substring(start, end);

    if (selectedText) {
      let sizeText = '';
      switch (size) {
        case '2':
          sizeText = 'petit';
          break;
        case '3':
          sizeText = 'normal';
          break;
        case '4':
          sizeText = 'grand';
          break;
        default:
          return; // Don't do anything for empty selection
      }

      const wrappedText = `[${sizeText}]${selectedText}[/${sizeText}]`;

      const newValue = textarea.value.substring(0, start) + wrappedText + textarea.value.substring(end);

      // Update the form control value
      this.derogationForm.patchValue({commentaire: newValue});

      setTimeout(() => {
        const newCursorPos = start + wrappedText.length;
        textarea.setSelectionRange(newCursorPos, newCursorPos);
        textarea.focus();
      });
    } else {
      textarea.focus();
    }
  }

  // No need for onEditorInput since formControlName handles it automatically

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
      // The commentaire field will contain text with formatting markers
      console.log('Rich text comment:', this.derogationForm.get('commentaire')?.value);
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
