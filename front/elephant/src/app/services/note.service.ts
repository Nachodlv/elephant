import {Injectable} from '@angular/core';
import {HttpService} from './http.service';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {Note, Tags} from '../models/note-model';
import {map, tap} from 'rxjs/operators';
import {Comment} from '../models/comment-model';
import {SharedUser} from '../models/sharedUser-model';

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
  createDuplicate(id: number): Observable<Note>{
    return this.getNote(id);
    /*
    porahora hice que devuelva la misma nota porque falta el metodo de duplicar en el back, asi que la
    redireccion va a ser a la misma nota
     */
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

  hasOwnerPermission(noteId): Observable<boolean> {
    return this.getPermissions(noteId).pipe(map(res => {
      return res === 'Owner';
    }));
  }

  getAllPermissions(noteId): Observable<SharedUser[]> {
    return of([
      new SharedUser('mantecol@gmail.com', 'Editor'),
      new SharedUser('georgalo@gmail.com', 'Viewer'),
      new SharedUser('serenito@gmail.com', 'Editor'),
      new SharedUser('elpepe@gmail.com', 'Viewer'),
    ]);
  }

  editPermissions(noteId, editedPermissions): Observable<any> {
    return of({});
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
    return this.httpService.get(`/user/notes`).pipe(tap((_ => {
      }), err => console.error(err)
    ), map(res => {
      return res.body;
    }));
  }

  addTags(id, tags: Tags): Observable<any> {
    return this.httpService.put(`/note/addTags/${id}`, JSON.stringify(tags));
  }

  deleteNote(note: Note): Observable<any> {
    return this.httpService.delete(`/note/delete/${note.uuid}`);
  }

  saveNoteToEdit(noteData): void {
    this.noteToEdit.next(noteData);
  }

}
