import {Injectable} from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthService} from '../services/auth.service';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class LoginAuthGuard implements CanActivate {

  constructor(
    private router: Router,
    private authService: AuthService
  ) {
  }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    this.authService.authenticate().pipe(map(res => {
      console.log('res', res);
    }, error => {
      console.error(error);
    }));
    if (this.authService.authenticate()) {
      console.log('Login true', this.authService.authenticate());
      this.router.navigate(['/home']);
      return false;
    } else {
      console.log('Login false', this.authService.authenticate());
      return true;
    }
  }

}
