import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';


@Component({
  selector: 'app-note-creator',
  templateUrl: './note-creator.component.html',
  styleUrls: ['./note-creator.component.scss']
})
export class NoteCreatorComponent implements OnInit {
  noteForm: FormGroup;
  submitted = false;


  constructor(private formBuilder: FormBuilder) {
  }

  // tslint:disable-next-line:typedef
  ngOnInit() {
    this.noteForm = this.formBuilder.group({
      title: ['', Validators.required]
    });

  }

  // tslint:disable-next-line:typedef
  disabled() {
    return this.noteForm.value.title === '';
  }

  // tslint:disable-next-line:typedef
  createNote() {
    console.log(this.noteForm.controls.title);
    /*note: Note = new Note(id,title);
    la logica de cuando se hace una nota
    send to Back/ api
     */
  }

}
