import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {Router} from '@angular/router';
import {SnackbarService} from '../../services/snackbar.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit, OnDestroy {

  registerForm: FormGroup;
  registerSubscription: Subscription;

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private router: Router,
    private snackBar: SnackbarService
  ) {
  }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required],
    });
  }

  ngOnDestroy(): void {
    this.registerSubscription?.unsubscribe();
  }


  onSubmit(): void {

    if (this.registerForm.invalid) {
      this.snackBar.openSnackbar('¡Credenciales inválidas!', 0);
      return;
    }

    this.registerSubscription = this.userService.register(this.registerForm.value)
      .subscribe(
        data => {
          this.snackBar.openSnackbar('¡Registro Exitoso!', 0);
          this.router.navigate(['/']);
        },
        error => {
          if (error.status === 409) {
            this.snackBar.openSnackbar('El email utilizado ya está en uso, pruebe con otro', 0);
          }
          console.error(error);
        }
      );
  }

}
