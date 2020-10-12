import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {NoteService} from '../../services/note.service';
import {SnackbarService} from '../../services/snackbar.service';
import {Note} from '../../models/note-model';

@Component({
  selector: 'app-edit-note',
  templateUrl: './edit-note.component.html',
  styleUrls: ['./edit-note.component.scss']
})
export class EditNoteComponent implements OnInit, OnDestroy {

  noteId: string;
  noteData: Note;

  loading = true;
  finishedAutoSave = true;

  editForm: FormGroup;
  autoSaveSubscription: Subscription;
  finishedEditSubscription: Subscription;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private noteService: NoteService,
    private snackBar: SnackbarService
  ) {
  }

  ngOnInit(): void {
    this.noteId = this.route.snapshot.paramMap.get('id');

    this.editForm = this.formBuilder.group({
      title: ['', Validators.required],
      content: ['', Validators.required],
    });

    this.loadNote();
    setInterval(() => this.autoSave(), 20 * 1000);
  }

  setFormValues(): void {
    this.editForm.controls.title.setValue(this.noteData.title);
    this.editForm.controls.content.setValue(this.noteData.content);
  }

  ngOnDestroy(): void {
    this.autoSaveSubscription?.unsubscribe();
    this.finishedEditSubscription?.unsubscribe();
  }

  loadNote(): void {
    this.noteService.getNote(this.noteId).subscribe(res => {
      this.noteData = res;
      this.setFormValues();
      this.loading = false;
    }, error => {
      console.error(error);
      this.snackBar.openSnackbar('¡Ha ocurrido un error al cargar la nota!', 0);
    });
  }

  autoSave(): void {
    const editFormData = this.editForm.getRawValue();
    this.finishedAutoSave = false;
    this.autoSaveSubscription = this.noteService.autoSave(this.noteId, editFormData).subscribe(res => {
      setTimeout(() => {
        this.finishedAutoSave = true;
      }, 5 * 1000); // el timeout esta para ver el cambio en el icono de autosave en el mockeo
    }, error => {
      console.error(error);
      this.snackBar.openSnackbar('¡Ha ocurrido un error en el guardado automatico!', 0);
    });
  }

  onSubmit(): void {
    const editFormData = this.editForm.getRawValue();
    this.finishedEditSubscription = this.noteService.finishedEdit(this.noteId, editFormData).subscribe(res => {
      this.router.navigate(['/note/', this.noteId]);
      this.snackBar.openSnackbar('Se ha editado con éxito la nota', 0);
    }, error => {
      console.error(error);
      this.snackBar.openSnackbar('¡Ha ocurrido un error terminar la edición de la nota!', 0);
    });
  }

}
