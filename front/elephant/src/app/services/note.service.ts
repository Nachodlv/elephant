import {Injectable} from '@angular/core';
import {HttpService} from './http.service';
import {Observable, of} from 'rxjs';
import {Note, Tags} from '../models/note-model';
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
      console.error(err);
    }), map(response => {
      return Note.fromJson(response.body);
    }));
  }

  getNote(id): Observable<Note> {
    return this.httpService.get(`/note/${id}`).pipe(tap((_ => {
      }), err => console.error(err)
    ), map(res => {
      return Note.fromJson(res.body);
    }));
  }

  shareNote(noteId, shareNoteData): Observable<any> {
    return this.httpService.put(`/${noteId}/permission/add`, JSON.stringify(shareNoteData));
  }

  getComments(id): Observable<Comment[]> {
    return this.httpService.get(`/comment/all/${id}`).pipe(tap((_ => {
      }), err => console.error(err)
    ), map(res => {
      const comments = res.body;
      comments.forEach(comment => Comment.fromJson(comment));
      return comments;
    }));
  }

  getAllNotes(): Observable<Note[]> {
    return of([
      {
        uuid: 1, title: 'Note First', content: 'justcontent', created: 'today', tags: ['lab2']
      }, {
        uuid: 2, title: 'Note Second', content: 'justcontent', created: 'today', tags: ['lab1', 'elephant']
      }, {
        uuid: 3, title: 'Note Third', content: 'justcontent', created: 'today', tags: ['lab2', 'elephant']
      }, {
        uuid: 4, title: 'Note Fourth', content: 'justcontent', created: 'today', tags: ['lab1']
      }, {
        uuid: 5, title: 'Note Fifth', content: 'justcontent', created: 'today', tags: ['lab1', 'lab2']
      }, {
        uuid: 6, title: 'Note Sixth', content: 'justcontent', created: 'today', tags: ['lab1', 'lab2', 'elephant']
      },
    ]);
  }
  addTags(id, tags: Tags): Observable<any> {
    return this.httpService.put(`/note/addTags/${id}`, JSON.stringify(tags));
  }

}
