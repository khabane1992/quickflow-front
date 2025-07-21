import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DerogationRequestComponent } from './derogation-request.component';

describe('DerogationRequestComponent', () => {
  let component: DerogationRequestComponent;
  let fixture: ComponentFixture<DerogationRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DerogationRequestComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DerogationRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
