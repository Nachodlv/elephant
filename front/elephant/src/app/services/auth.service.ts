import { Injectable } from '@angular/core';
import {JwtClientService} from './jwt-client.service';
import {Observable} from 'rxjs';
import {User} from '../models/user-model';
import {map} from 'rxjs/operators';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(public jwtClientService: JwtClientService, public router: Router) { }


  login(user): Observable<User> {
    const formUser = this.createLoginUser(user);
    return this.jwtClientService.generateToken(formUser).pipe(map(res => {
      const resToken = res.headers.get('Authorization');
      const tokenArray = resToken.split(' ');
      localStorage.setItem('user', tokenArray[1]);
      return formUser;
    }));
  }

  authenticate(): Observable<boolean> {
    return this.jwtClientService.authenticate().pipe(map(res => {
      if (res.body === true){
        return true;
      } else {
        localStorage.setItem('user', undefined);
        this.router.navigate(['/']);
        return false;
      }
    }));
  }

  createLoginUser(user): User {
    const email = user.email;
    const password = user.password;
    return new User('', '', email, password);
  }

  isLoggedIn(): boolean{
    if (localStorage.getItem('user') === undefined){
      return false;
    }
    else{
      return true;
    }
  }
}
