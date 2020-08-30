import { Injectable } from '@angular/core';
import {JwtClientService} from './jwt-client.service';
import {Observable, of} from 'rxjs';
import {User} from '../models/user-model';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  public token;
  public valid;

  constructor(public jwtClientService: JwtClientService) { }


  login(user): Observable<User> {
    const formUser = this.createLoginUser(user);
    return this.jwtClientService.generateToken(formUser).pipe(map(res => {
      this.token = res.token;
      return formUser;
    }));
  }

  authenticate(): boolean {
    this.jwtClientService.authenticate(this.token).subscribe(res => {
      this.valid = res.valid;
    });
    return this.valid;
  }

  createLoginUser(user): User {
    const email = user.email;
    const password = user.password;
    return new User('', '', email, password);
  }
}
