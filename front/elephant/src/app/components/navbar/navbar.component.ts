import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {SnackbarService} from '../../services/snackbar.service';
import {UserService} from '../../services/user.service';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {

  constructor(private router: Router,
              private snackbar: SnackbarService,
              private userService: UserService,
              private authService: AuthService,
  ) {
  }

  ngOnInit(): void {
  }

  registerUser(): void {
    this.router.navigate(['/register']);
  }

  seeProfile(): void {
    this.router.navigate(['/profile']);
  }

  logout(): void {
    this.userService.logout().subscribe(res => {
        if (res === 'Success') {
          this.router.navigate(['']).then(() => this.snackbar.openSnackbar('Usted ha cerrado sesión correctamente!'));
        } else {
          this.snackbar.openSnackbar('No se pudo cerrar sesion!');
        }
      }, error => {
        this.snackbar.openSnackbar('No se pudo cerrar sesion!', 0);
        console.error(error);
      }
    );
  }

  isLoggedIn(): boolean {
    return localStorage.getItem('user') !== null;
  }
}
