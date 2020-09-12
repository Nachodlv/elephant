import {Injectable} from '@angular/core';
import {HttpService} from './http.service';
import {Observable, of} from 'rxjs';
import {Note} from '../models/note-model';
import {map, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})

export class NoteService {

  constructor(private httpService: HttpService) {
  }

  createNote(note: Note): Observable<Note> {
    return this.httpService.post('/note/new', JSON.stringify(note)).pipe(tap((_ => {
    }), err => {
      console.log(err);
    }), map(response => {
      return Note.fromJson(response.body);
    }));
  }

  getNote(id): Observable<Note> {
    return this.httpService.get(`/note/${id}`).pipe(tap((_ => {
      }), err => console.log(err)
    ), map(res => {
      return Note.fromJson(res.body);
    }));
  }

  shareNote(shareNoteData): Observable<any> {
    return of({value: true});
  }
}
