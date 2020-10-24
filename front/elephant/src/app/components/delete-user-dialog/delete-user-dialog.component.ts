import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Subscription} from 'rxjs';
import {SnackbarService} from '../../services/snackbar.service';
import {UserService} from '../../services/user.service';
import {MatDialogRef} from '@angular/material/dialog';
import {Router} from '@angular/router';

@Component({
  selector: 'app-delete-user-dialog',
  templateUrl: './delete-user-dialog.component.html',
  styleUrls: ['./delete-user-dialog.component.scss']
})
export class DeleteUserDialogComponent implements OnInit, OnDestroy {

  form: FormGroup;
  deleteUserSubscription: Subscription;

  constructor(
    private formBuilder: FormBuilder,
    private snackBar: SnackbarService,
    private userService: UserService,
    public dialogRef: MatDialogRef<DeleteUserDialogComponent>,
    public router: Router
  ) {
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      password: ['', Validators.required],
    });
  }

  ngOnDestroy(): void {
    this.deleteUserSubscription?.unsubscribe();
  }

  onSubmit(): void {
    this.deleteUserSubscription = this.userService.deleteUser(this.form.value).subscribe(res => {
      this.snackBar.openSnackbar('¡Se ha eliminado su cuenta con éxito!', 0);
      this.dialogRef.close();
      this.router.navigate(['']);
    }, error => {
      console.error(error);
      if (error.status === 401) {
        this.snackBar.openSnackbar('¡La contraseña es incorrecta!', 0);
      } else {
        this.snackBar.openSnackbar('¡Ha ocurrido un error, vuelva a intentarlo!', 0);
      }
    });
  }

}
