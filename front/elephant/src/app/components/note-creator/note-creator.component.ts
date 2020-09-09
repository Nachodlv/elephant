import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SnackbarService} from '../../services/snackbar.service';
import {NoteService} from '../../services/note.service';
import {Note} from '../../models/Note';
import {Router} from '@angular/router';


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
  ) {
  }

  ngOnInit(): void {
    this.noteForm = this.formBuilder.group({
      title: ['', Validators.required]
    });

  }

  disabled(): boolean {
    return this.noteForm.value.title === '';
  }

  createNote(): void {
    const title = this.noteForm.value.title;
    console.log(title);
    this.noteService.createNote(new Note(null, title)).subscribe(res => {
      this.snackBar.openSnackbar('¡Creación de Nota Exitosa!', 0);
      this.router.navigate(['/note/', res.uuid]);
    }, error => {
      this.snackBar.openSnackbar('¡Ha ocurrido un error!', 0);
    });
  }

}
