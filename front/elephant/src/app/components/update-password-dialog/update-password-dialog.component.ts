import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Subscription} from 'rxjs';
import {SnackbarService} from '../../services/snackbar.service';
import {UserService} from '../../services/user.service';
import {MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-update-password-dialog',
  templateUrl: './update-password-dialog.component.html',
  styleUrls: ['./update-password-dialog.component.scss']
})
export class UpdatePasswordDialogComponent implements OnInit, OnDestroy {

  form: FormGroup;
  updatePasswordSubscription: Subscription;

  constructor(
    private formBuilder: FormBuilder,
    private snackBar: SnackbarService,
    private userService: UserService,
    public dialogRef: MatDialogRef<UpdatePasswordDialogComponent>
  ) {
  }

  ngOnInit(): void {

    this.form = this.formBuilder.group({
      oldPassword: ['', Validators.required],
      newPassword: ['', Validators.required],
      repeatPassword: ['', Validators.required],
    });
  }

  ngOnDestroy(): void {
    this.updatePasswordSubscription?.unsubscribe();
  }

  onSubmit(): void {
    this.updatePasswordSubscription = this.userService.updatePassword(this.form.value).subscribe(res => {
      this.snackBar.openSnackbar('¡Se ha modificado la contraseña con éxito!', 0);
      this.dialogRef.close();
    }, error => {
      this.snackBar.openSnackbar('¡Ha ocurrido un error, vuelva a intentarlo!', 0);
    });
  }

}
