import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Subscription} from 'rxjs';
import {ActivatedRoute} from '@angular/router';
import {NoteService} from '../../services/note.service';

@Component({
  selector: 'app-edit-note',
  templateUrl: './edit-note.component.html',
  styleUrls: ['./edit-note.component.scss']
})
export class EditNoteComponent implements OnInit, OnDestroy {

  noteId: string;

  noteData;

  editForm: FormGroup;
  startEditSubscription: Subscription;
  autoSaveSubscription: Subscription;
  finishedEditSubscription: Subscription;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private noteService: NoteService
  ) {
  }

  ngOnInit(): void {
    this.noteId = this.route.snapshot.paramMap.get('id');

    this.editForm = this.formBuilder.group({
      title: ['', Validators.required],
      content: ['', Validators.required],
    });

    this.startEditing();
  }

  setFormValues(): void {
    this.editForm.controls.title.setValue(this.noteData.title);
    this.editForm.controls.content.setValue(this.noteData.content);
  }

  ngOnDestroy(): void {
    this.startEditSubscription?.unsubscribe();
    this.autoSaveSubscription?.unsubscribe();
    this.finishedEditSubscription?.unsubscribe();
  }

  startEditing(): void {
    this.startEditSubscription = this.noteService.startEdit(this.noteId).subscribe(res => {
      console.log(res);
      this.noteData = res;
      this.setFormValues();
    }, error => {
      console.error(error);
    });
  }

  onSubmit(): void {
    const editFormData = this.editForm.getRawValue();
    this.finishedEditSubscription = this.noteService.finishedEdit(this.noteId, editFormData).subscribe(res => {
      console.log(res);
    }, error => {
      console.error(error);
    });
  }

}
