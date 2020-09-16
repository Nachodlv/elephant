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

  getComments(id): Observable<any> {
    return of([
      {
        uuid: 1,
        title: 'com title',
        content: 'Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum',
        owner: 'juan pepito',
        created: '2020-09-15T20:50:24.436+0000'
      },
      {
        uuid: 2,
        title: 'com title',
        content: `Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.`,
        owner: 'juan pepito',
        created: '2020-09-10T20:03:48.436+0000'
      },
      {
        uuid: 3,
        title: 'com title',
        content: `Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.`,
        owner: 'juan pepito',
        created: '2020-09-10T20:03:48.436+0000'
      },
      {
        uuid: 4,
        title: 'com title',
        content: `Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.`,
        owner: 'juan pepito',
        created: '2020-09-10T20:03:48.436+0000'
      },
      {
        uuid: 5,
        title: 'com title',
        content: `Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.`,
        owner: 'juan pepito',
        created: '2020-09-10T20:03:48.436+0000'
      }
    ]);
  }
}
