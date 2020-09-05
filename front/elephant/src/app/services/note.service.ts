import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpService} from './http.service';
import {Note} from '../models/Note';
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
}
