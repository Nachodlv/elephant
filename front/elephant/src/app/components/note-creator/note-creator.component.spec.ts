import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NoteCreatorComponent } from './note-creator.component';

describe('NoteCreatorComponent', () => {
  let component: NoteCreatorComponent;
  let fixture: ComponentFixture<NoteCreatorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NoteCreatorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NoteCreatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
