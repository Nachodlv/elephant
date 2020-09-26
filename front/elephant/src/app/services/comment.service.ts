import {Injectable} from '@angular/core';
import {Note} from '../models/note-model';
import {Observable} from 'rxjs';
import {map, tap} from 'rxjs/operators';
import {HttpService} from './http.service';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private httpService: HttpService) {
  }

  createComment(idNote, comment ): Observable<any> {
    return this.httpService.post('/comment/add/' + idNote , JSON.stringify(comment)).pipe(tap((_ => {
    }), err => {
      console.log(err);
    }), map(response => {
      return Note.fromJson(response.body);
    }));
  }
}
