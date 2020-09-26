import {Component, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {CommentService} from '../../services/comment.service';
import {Comment} from '../../models/comment';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss']
})
export class CommentComponent implements OnInit {
  comment: FormControl;

  constructor(private commentService: CommentService) {
  }

  ngOnInit(): void {
    this.comment = new FormControl('', []);
  }

  onSubmitComment(): void {
    if (this.comment.value.trim()) {
      const newComment: Comment = new Comment(this.comment.value.trim());
      // this.commentService()
    }
    console.log('Comment value: ', this.comment.value);
  }

}
