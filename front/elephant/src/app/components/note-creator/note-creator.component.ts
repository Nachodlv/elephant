import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SnackbarService} from '../../services/snackbar.service';
import {NoteService} from '../../services/note.service';
import {Note} from '../../models/Note';


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
    }, error => {
      this.snackBar.openSnackbar('¡Ha ocurrido un error!', 0);
    });
    /*note: Note = new Note(id,title);
    la logica de cuando se hace una nota
    send to Back/ api
     */
  }

}
