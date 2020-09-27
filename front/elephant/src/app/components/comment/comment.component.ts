import {Component, Input, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {CommentService} from '../../services/comment.service';
import {Comment} from '../../models/comment-model';
import {SnackbarService} from '../../services/snackbar.service';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss']
})
export class CommentComponent implements OnInit {
  comment: FormControl;
  @Input() noteId: number;

  constructor(private commentService: CommentService,
              private snackBar: SnackbarService,
  ) {
  }

  ngOnInit(): void {
    this.comment = new FormControl('', []);
  }

  onSubmitComment(): void {
    if (this.comment.value.trim()) {
      const newComment: Comment = new Comment(this.comment.value.trim());
      this.commentService.createComment(this.noteId, newComment).subscribe(res => {
        this.snackBar.openSnackbar('¡Creación de Comentario Exitoso!', 0);
      });
    }
    else{
      this.snackBar.openSnackbar('No se puede crear un comentario vacio. Intentelo nuevamente!', 0);
    }
    console.log('Comment value: ', this.comment.value);
  }

}
