import {Component, OnInit} from '@angular/core';
import {UserService} from '../../services/user.service';
import {User} from '../../models/user-model';


@Component({
  selector: 'app-see-profile',
  templateUrl: './see-profile.component.html',
  styleUrls: ['./see-profile.component.scss']
})
export class SeeProfileComponent implements OnInit {

  user: User;

  constructor(private userService: UserService) {
  }

  ngOnInit(): void {
    this.userService.getUser().subscribe(res => {
      this.user = res;
      console.log(this.user);
    });

  }
  saveProfile(): void{
  }

}
