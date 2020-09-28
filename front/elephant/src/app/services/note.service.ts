import {Injectable} from '@angular/core';
import {HttpService} from './http.service';
import {Observable} from 'rxjs';
import {Note} from '../models/note-model';
import {map, tap} from 'rxjs/operators';
import {Comment} from '../models/comment-model';

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

  shareNote(noteId, shareNoteData): Observable<any> {
    return this.httpService.put(`/${noteId}/permission/add`, JSON.stringify(shareNoteData));
  }

  getComments(id): Observable<Comment[]> {
    return this.httpService.get(`/comment/all/${id}`).pipe(tap((_ => {
      }), err => console.log(err)
    ), map(res => {
      const comments = res.body;
      comments.forEach(comment => Comment.fromJson(comment));
      return comments;
    }));
  }

}
