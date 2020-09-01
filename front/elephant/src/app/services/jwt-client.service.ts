import { Injectable } from '@angular/core';
import {HttpService} from './http.service';
import {Observable, of} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class JwtClientService {

  constructor(private httpService: HttpService) { }

  public generateToken(request): Observable<any> {
     return this.httpService.post('/login', request);
  }

  public authenticate(): Observable<any>{
    return this.httpService.get('/token/verify');
  }
}
