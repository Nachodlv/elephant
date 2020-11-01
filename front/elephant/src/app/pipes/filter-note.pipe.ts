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

  transform(notes: Note[], filter?: string): any {
    if (!notes || !filter) return notes;
    return notes.filter(note => FilterNotePipe.containsFilterNote(note, filter.toLowerCase()));
  }
}
