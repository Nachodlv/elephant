import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {Router} from '@angular/router';
import {SnackbarService} from '../../services/snackbar.service';
import {Subscription} from 'rxjs';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {

  loginForm: FormGroup;
  loginSubscription: Subscription;

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private router: Router,
    private snackBar: SnackbarService,
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  ngOnDestroy(): void {
    this.loginSubscription?.unsubscribe();
  }

  onSubmit(): void {

    if (this.loginForm.invalid) {
      return;
    }

    this.loginSubscription = this.authService.login(this.loginForm.value)
      .subscribe(
        data => {
          this.snackBar.openSnackbar('¡Login Exitoso!', 0);
          this.router.navigate(['/home']);
        },
        error => {
          this.snackBar.openSnackbar('El email o la contraseña son incorrectos', 0);
          console.error(error);
        }
      );
  }

}
