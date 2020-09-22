import {Component, Input, OnInit} from '@angular/core';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {MatChipInputEvent} from '@angular/material/chips';
import {NoteService} from '../../services/note.service';

@Component({
  selector: 'app-tag-note',
  templateUrl: './tag-note.component.html',
  styleUrls: ['./tag-note.component.scss']
})
export class TagNoteComponent implements OnInit {

  selectable = true;
  removable = true;
  addOnBlur = true;
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];
  tags: string[] = [];
  @Input() noteId: number;

  constructor(private noteService: NoteService
  ) {
  }

  ngOnInit(): void {
    this.loadTags();
  }

  loadTags(): void {
    this.noteService.getTagsByNote(this.noteId).subscribe(res => {
      this.tags = res;
    });
  }

  add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;

    if ((value || '').trim()) {
      this.tags.push(value.trim());
    }

    // Reset the input value
    if (input) {
      input.value = '';
    }
    console.log(this.tags);
  }

  remove(tag): void {
    const index = this.tags.indexOf(tag);

    if (index >= 0) {
      this.tags.splice(index, 1);
    }
    console.log(this.tags);
  }
}
