import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {MatChipInputEvent} from '@angular/material/chips';
import {NoteService} from '../../services/note.service';
import {Tags} from '../../models/note-model';
import {SnackbarService} from '../../services/snackbar.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-tag-note',
  templateUrl: './tag-note.component.html',
  styleUrls: ['./tag-note.component.scss']
})
export class TagNoteComponent implements OnInit, OnDestroy {

  selectable = true;
  removable = true;
  addOnBlur = true;
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];
  @Input() tags: string[] = [];
  @Input() noteId: number;
  tagSubscription: Subscription;

  constructor(private noteService: NoteService,
              private snackBar: SnackbarService
  ) {
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.tagSubscription?.unsubscribe();
  }

  add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;

    if ((value || '').trim()) {
      this.tags.push(value.trim());
      this.tagSubscription = this.noteService.addTags(this.noteId, new Tags(this.tags)).subscribe(res => {}, error => {
        this.snackBar.openSnackbar('¡Ha ocurrido un error!', 0);
      });
    }

    // Reset the input value
    if (input) {
      input.value = '';
    }
  }

  remove(tag): void {
    const index = this.tags.indexOf(tag);

    if (index >= 0) {
      this.tags.splice(index, 1);
      this.tagSubscription = this.noteService.addTags(this.noteId, new Tags(this.tags)).subscribe(res => {}, error => {
        this.snackBar.openSnackbar('¡Ha ocurrido un error!', 0);
      });
    }
  }
}
