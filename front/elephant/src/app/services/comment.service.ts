import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {HttpService} from './http.service';
import {Comment} from '../models/comment-model';
import {map, tap} from 'rxjs/operators';
import {Note} from '../models/note-model';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private httpService: HttpService) {
  }

  createComment(idNote, comment: Comment): Observable<Note> {
    return this.httpService.post('/comment/add/' + idNote, JSON.stringify(comment)).pipe(tap((_ => {
    }), err => {
      console.log(err);
    }), map(response => {
      return Note.fromJson(response.body);
    }));
  }
}
