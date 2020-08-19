import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {HttpService} from "./http.service";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpService: HttpService) { }

  register(user): Observable<any>{
    console.log("Se hizo sumbit del form", user);

    return this.httpService.post('/user/register', user);
  }
}
