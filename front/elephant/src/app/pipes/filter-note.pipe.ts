import {Pipe, PipeTransform} from '@angular/core';
import {Note} from '../models/note-model';

@Pipe({
  name: 'filterNote',
})
export class FilterNotePipe implements PipeTransform {

  static containsFilterNote(note: Note, filter: string): boolean {
    return note.title.toLowerCase().includes(filter) ||
      note.tags.some(tag => tag.toLowerCase().includes(filter));
  }

  static sortNotesByFavourite(noteList: Note[]): Note[] {
    return noteList.slice().sort((a: Note, b: Note) => {
      if (a.pinUp) {
        return -1;
      } else if (b.pinUp) {
        return 1;
      }
      return 0;
    });
  }

  transform(notes: Note[], filter?: string): any {
    notes = FilterNotePipe.sortNotesByFavourite(notes);
    if (!notes || !filter) return notes;
    return notes.filter(note => FilterNotePipe.containsFilterNote(note, filter.toLowerCase()));
  }
}
