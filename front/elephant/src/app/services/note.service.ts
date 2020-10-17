import {Injectable} from '@angular/core';
import {HttpService} from './http.service';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {Note, Tags} from '../models/note-model';
import {map, tap} from 'rxjs/operators';
import {Comment} from '../models/comment-model';

@Injectable({
  providedIn: 'root'
})
export class NoteService {

  private noteToEdit = new BehaviorSubject([]);
  noteData = this.noteToEdit.asObservable();

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

  getPermissions(noteId): Observable<any> {
    return this.httpService.getText(`/${noteId}/permission`).pipe(tap((_ => {
      }), err => console.error(err)
    ), map(res => {
      return res.body;
    }));
  }

  hasEditPermission(noteId): Observable<boolean> {
    return this.getPermissions(noteId).pipe(map(res => {
      return (res === 'Editor' || res === 'Owner');
    }));
  }

  startEdit(noteId): Observable<boolean> {
    return this.httpService.get(`/note/startEdit/${noteId}`).pipe(tap((_ => {
      }), err => console.error(err)
    ), map(res => {
      return res.body;
    }));
  }

  autoSave(noteId, editedNoteData): Observable<any> {
    return this.httpService.put(`/note/autoSave/${noteId}`, JSON.stringify(editedNoteData));
  }

  endEdit(noteId, editedNoteData): Observable<any> {
    return this.httpService.put(`/note/endEdit/${noteId}`, JSON.stringify(editedNoteData));
  }

  getAllNotes(): Observable<Note[]> {
    return of([
      {
        uuid: 1,
        title: 'Note First',
        content: 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
        created: '2020-10-16',
        tags: ['lab2']
      }, {
        uuid: 2,
        title: 'Note Second',
        content: 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an un',
        created: '2020-10-16',
        tags: ['lab1', 'elephant', 'elephant', 'elephant', 'elephant', 'elephant', 'elephant']
      }, {
        uuid: 3, title: 'Note Third', content: 'justcontent', created: '2020-10-16', tags: ['lab2', 'elephant']
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

  deleteNote(note): Observable<any> {
    return of(note);
  }

  saveNoteToEdit(noteData): void {
    this.noteToEdit.next(noteData);
  }

}
