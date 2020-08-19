import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../services/user.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  registerForm: FormGroup;
  success = false;
  showError = false;


  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private router: Router
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
      return;
    }

    this.userService.register(this.registerForm.value)
      .subscribe(
        data => {
          this.success = true;
          this.router.navigate(['/login']);
        },
        error => {
          this.showError = true;
          console.error(error);
        }
      )
  }

}
