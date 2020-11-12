import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SnackbarService} from '../../services/snackbar.service';
import {NoteService} from '../../services/note.service';
import {Router} from '@angular/router';
import {Note} from '../../models/note-model';
import {MatDialogRef} from '@angular/material/dialog';


@Component({
  selector: 'app-note-creator',
  templateUrl: './note-creator.component.html',
  styleUrls: ['./note-creator.component.scss']
})
export class NoteCreatorComponent implements OnInit {
  noteForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private snackBar: SnackbarService,
              private noteService: NoteService,
              private router: Router,
              public dialogRef: MatDialogRef<NoteCreatorComponent>
  ) {
  }

  ngOnInit(): void {
    this.noteForm = this.formBuilder.group({
      title: ['', Validators.required],
      template: ['noTemplate', Validators.required]
    });

  }

  createNote(): void {
    const title = this.noteForm.value.title;
    this.noteService.createNote(new Note(null, title)).subscribe(res => {
      this.snackBar.openSnackbar('¡Creación de Nota Exitosa!', 0);
      this.dialogRef.close();
      this.router.navigate(['/note/', res.uuid]);
    }, error => {
      this.snackBar.openSnackbar('¡Ha ocurrido un error!', 0);
    });
  }
}
