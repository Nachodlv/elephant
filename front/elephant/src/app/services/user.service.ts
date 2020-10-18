import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpService} from './http.service';
import {User} from '../models/user-model';
import {map, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UserService {


  constructor(private httpService: HttpService) {
  }

  register(user): Observable<User> {
    const formUser = this.createUser(user);

    return this.httpService.post('/user/create', JSON.stringify(formUser))
      .pipe(tap((_ => {
        }), err => console.error(err)
      ), map(res => {
        return User.fromJson(res.body);
      }));
  }

  createUser(user): User {
    const fullName = user.fullName;
    const firstName = fullName.split(' ').slice(0, -1).join(' ');
    const lastName = fullName.split(' ').slice(-1).join(' ');
    const email = user.email;
    const password = user.password;
    return new User(firstName, lastName, email, password);
  }

  getUser(): Observable<User> {
    return this.httpService.get('/user').pipe(tap((_ => {
      }), err => console.error(err)
    ), map(res => {
      return User.fromJson(res.body);
    }));
  }

  updatePassword(passwordData): Observable<any> {
    return this.httpService.put('/user/updatePassword', JSON.stringify(passwordData))
      .pipe(tap((_ => {
        }), err => console.error(err)
      ));
  }
  updateUserName(user, fullName): any{
    if (fullName.trim()){
    const firstName = fullName.split(' ').slice(0, -1).join(' ');
    const lastName = fullName.split(' ').slice(-1).join(' ');
    user.updateName(firstName, lastName);
    return this.httpService.put('/user/editUser', {firstName, lastName} );
    }
  }

  logout(): Observable<string> {
    return this.httpService.post('/logout', '').pipe(tap((_ => {
      }), err => console.error(err)
    ), map(res => {
      localStorage.removeItem('user');
      return 'Success';
    }));
  }
}
