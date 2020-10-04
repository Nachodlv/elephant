import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {SnackbarService} from '../../services/snackbar.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {

  constructor(private router: Router,
              private snackbar: SnackbarService,
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
    this.router.navigate(['']).then(() => this.snackbar.openSnackbar('Usted ha cerrado sesión correctamente!'));
  }
}
