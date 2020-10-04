import {Component, OnInit} from '@angular/core';
import {UserService} from '../../services/user.service';
import {User} from '../../models/user-model';
import {UpdatePasswordDialogComponent} from '../update-password-dialog/update-password-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {SnackbarService} from '../../services/snackbar.service';


@Component({
  selector: 'app-see-profile',
  templateUrl: './see-profile.component.html',
  styleUrls: ['./see-profile.component.scss']
})
export class SeeProfileComponent implements OnInit {

  user: User;
  loading = true;
  updating = false;
  fullName: string;

  constructor(
    private userService: UserService,
    private dialog: MatDialog,
    private snackbar: SnackbarService,
  ) {
  }

  ngOnInit(): void {
    this.userService.getUser().subscribe(res => {
      this.user = res;
      this.loading = false;
      this.fullName = this.user.firstName + ' ' + this.user.lastName;
    });
  }

  saveProfile(): void {

    this.userService.updateUserName(this.user, this.fullName).subscribe(res => {
      this.snackbar.openSnackbar('¡Cambio de Nombre exitoso!', 0);
    });
    this.updating = false;
  }


  openUpdatePasswordDialog(): void {
    this.dialog.open(UpdatePasswordDialogComponent, {
      width: '26vw',
      position: {top: '10%'}
    });
  }
}
