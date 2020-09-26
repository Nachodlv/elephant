import {Component, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss']
})
export class CommentComponent implements OnInit {
  comment: FormControl;

  constructor() {
  }

  ngOnInit(): void {
    this.comment = new FormControl('', []);
  }

  onSubmitComment(): void {
    console.log('Comment value: ', this.comment.value);
  }

}
