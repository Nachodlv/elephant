import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../services/user.service";
import {Router} from "@angular/router";
import {SnackbarService} from "../../services/snackbar.service";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  registerForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private router: Router,
    private snackBar : SnackbarService
  ) { }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required],
    });
  }


  onSubmit(): void {

    if(this.registerForm.invalid){
      this.snackBar.openSnackbar("¡Credenciales invalidas!", 0);
      return;
    }

    this.userService.register(this.registerForm.value)
      .subscribe(
        data => {
          this.snackBar.openSnackbar("¡Registro Exitoso!", 0);
          //this.router.navigate(['/login']);
        },
        error => {
          console.error(error);
        }
      )
  }

}
