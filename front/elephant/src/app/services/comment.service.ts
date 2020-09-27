import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {HttpService} from './http.service';
import {Comment} from '../models/comment-model';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private httpService: HttpService) {
  }

  createComment(idNote, comment: Comment): Observable<any> {
    return of(comment);
  }
}
