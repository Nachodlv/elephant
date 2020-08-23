import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {HttpService} from './http.service';
import {User} from '../models/user-model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpService: HttpService) {
  }

  register(user): Observable<User> {
    const formUser = this.createUser(user);

    return of(formUser);
    // return this.httpService.post('/user/register', formUser);
  }

  createUser(user): User {
    const fullName = user.fullName;
    const firstName = fullName.split(' ').slice(0, -1).join(' ');
    const lastName = fullName.split(' ').slice(-1).join(' ');
    const email = user.email;
    const password = user.password;
    return new User(firstName, lastName, email, password);
  }
}
