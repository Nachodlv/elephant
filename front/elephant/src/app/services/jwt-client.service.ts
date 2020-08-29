import { Injectable } from '@angular/core';
import {HttpService} from './http.service';
import {Observable, of} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class JwtClientService {

  constructor(private httpService: HttpService) { }

  public generateToken(request): Observable<any> {
    return of({token: 'tokenFromBack'});
    // return this.httpService.post('/login', request);
  }

  public authenticate(token): Observable<any>{
    return of({valid: true});
    // return this.httpService.get('/validateToken', token);
  }
}
