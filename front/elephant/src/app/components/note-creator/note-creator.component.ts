import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Note} from "../../note";

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

  ngOnInit() {
    this.noteForm = this.formBuilder.group({
      title: ["", Validators.required]
    })

  }

 disabled(){return this.noteForm.value.title==="";}

  createNote() {
    console.log(this.noteForm.controls.title);
    //note: Note = new Note(id,title);
    //la logica de cuando se hace una nota
    //send to Back/ api
  }

}
