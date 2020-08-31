import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {NoteCreatorComponent} from '../note-creator/note-creator.component';

@Component({
  selector: 'app-note',
  templateUrl: './note.component.html',
  styleUrls: ['./note.component.scss']
})
export class NoteComponent implements OnInit {
  submitted = false;
  cardName: string;

  constructor(public dialog: MatDialog) {
  }

  openDialog(): void {
   this.dialog.open(NoteCreatorComponent, {
        width: '400px',
        height: '230px',
      });
  }

  ngOnInit(): void {
  }

  /*
  disabled(): boolean {
    return this.noteForm.value.title === '';
  }
  */
}


/*
export class NoteComponent implements OnInit {
  noteForm: FormGroup;
  submitted = false;

  constructor(private formBuilder: FormBuilder) {
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
    console.log(this.noteForm.controls.title);
    note: Note = new Note(id,title);
    la logica de cuando se hace una nota
    send to Back/ api

  }
}
*/

