import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {SnackbarService} from '../../services/snackbar.service';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  logged: boolean;

  constructor(private router: Router,
              private snackbar: SnackbarService,
              private authService: AuthService,
  ) {
  }

  ngOnInit(): void {
    this.logged = this.authService.isLoggedIn();
  }

  registerUser(): void {
    if (this.logged === true){
    this.router.navigate(['/register']);
    }
  }

  seeProfile(): void {
    this.router.navigate(['/profile']);
  }

  logout(): void {
    this.router.navigate(['']).then(() => this.snackbar.openSnackbar('Usted ha cerrado sesión correctamente!'));
    this.logged = false;
  }
}
