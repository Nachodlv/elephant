import {Component, OnDestroy, OnInit} from '@angular/core';
import {NoteService} from '../../services/note.service';
import {ActivatedRoute, Router} from '@angular/router';
import {SnackbarService} from '../../services/snackbar.service';
import {MatDialog} from '@angular/material/dialog';
import {ShareNoteDialogComponent} from '../share-note-dialog/share-note-dialog.component';
import {Subscription} from 'rxjs';
import {Comment} from '../../models/comment-model';

@Component({
  selector: 'app-see-note',
  templateUrl: './see-note.component.html',
  styleUrls: ['./see-note.component.scss']
})
export class SeeNoteComponent implements OnInit, OnDestroy {

  public id;
  public title;
  public content;
  public created;

  comments: Comment[] = [];

  noteSubscription: Subscription;
  commentsSubscription: Subscription;

  constructor(
    private noteService: NoteService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: SnackbarService,
    private dialog: MatDialog
  ) {
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');

    this.loadNote();
    this.loadComments();
  }

  ngOnDestroy(): void {
    this.noteSubscription?.unsubscribe();
    this.commentsSubscription?.unsubscribe();
  }

  loadNote(): void {
    this.noteSubscription = this.noteService.getNote(this.id).subscribe(res => {
      this.title = res.title;
      this.content = res.content;

      const timeStamp = res.created.split('T');
      this.created = timeStamp[0];
    }, error => {
      this.snackBar.openSnackbar('¡Ha ocurrido un error, vuelva a intentarlo!', 0);
      this.router.navigate(['/home']);
    });
  }

  openDialog(): void {
    this.dialog.open(ShareNoteDialogComponent, {
      width: '30vw',
      position: {top: '10%'}
    });
  }

  loadComments(): void {
    this.commentsSubscription = this.noteService.getComments(this.id).subscribe(res => {
      this.comments = this.resolveCommentsData(res);
    }, error => {
      this.snackBar.openSnackbar('¡Ha ocurrido un error al cargar los comentarios!', 0);
    });
  }

  resolveCommentsData(comments): Comment[] {
    return comments.map(comment => {
      return {...comment, created: this.resolveCommentDate(comment.created)};
    });
  }

  resolveCommentDate(date): string {
    const minutes = 1000 * 60;
    const hours = 1000 * 60 * 60;
    const day = 1000 * 60 * 60 * 24;

    const dateNow = new Date().getTime();
    const dateOffset = (new Date().getTimezoneOffset() * minutes);
    const dateNowWithoutOffset = new Date(dateNow - dateOffset).getTime();
    const commentDate = new Date(date).getTime();
    const dateDifference = (dateNowWithoutOffset - commentDate);

    const minutesDifference = Math.floor(dateDifference / minutes);
    const hoursDifference = Math.floor(dateDifference / hours);
    const daysDifference = Math.floor(dateDifference / day);

    if (hoursDifference >= 24) {
      return daysDifference + (daysDifference !== 1 ? ' dias' : ' dia');
    } else if (minutesDifference >= 60){
      return hoursDifference + (hoursDifference !== 1 ? ' horas' : ' hora');
    } else {
      return minutesDifference + (minutesDifference !== 1 ? ' minutos' : ' minuto');
    }
  }

}
