import {Component, OnInit} from '@angular/core';
import {UserService} from '../../services/user.service';
import {User} from '../../models/user-model';
import {UpdatePasswordDialogComponent} from '../update-password-dialog/update-password-dialog.component';
import {MatDialog} from '@angular/material/dialog';


@Component({
  selector: 'app-see-profile',
  templateUrl: './see-profile.component.html',
  styleUrls: ['./see-profile.component.scss']
})
export class SeeProfileComponent implements OnInit {

  user: User;
  loading = true;

  constructor(
    private userService: UserService,
    private dialog: MatDialog
  ) {
  }

  ngOnInit(): void {
    this.userService.getUser().subscribe(res => {
      this.user = res;
      this.loading = false;
    });

  }

  saveProfile(): void {
  }

  openUpdatePasswordDialog(): void {
    this.dialog.open(UpdatePasswordDialogComponent, {
      width: '26vw',
      position: {top: '10%'}
    });
  }
}
