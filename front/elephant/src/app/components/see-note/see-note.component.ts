import {AfterViewChecked, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {NoteService} from '../../services/note.service';
import {ActivatedRoute, Router} from '@angular/router';
import {SnackbarService} from '../../services/snackbar.service';
import {MatDialog} from '@angular/material/dialog';
import {ShareNoteDialogComponent} from '../share-note-dialog/share-note-dialog.component';
import {Subscription} from 'rxjs';
import {Comment} from '../../models/comment-model';
import {isNotNullOrUndefined} from 'codelyzer/util/isNotNullOrUndefined';
import {EditNotePermissionsDialogComponent} from '../edit-note-permissions-dialog/edit-note-permissions-dialog.component';
import {Note} from '../../models/note-model';
import {DeleteNoteDialogFromNoteComponent} from '../delete-note-dialog-from-note/delete-note-dialog-from-note.component';
import {DeleteNoteDialogComponent} from "../delete-note-dialog/delete-note-dialog.component";

@Component({
  selector: 'app-see-note',
  templateUrl: './see-note.component.html',
  styleUrls: ['./see-note.component.scss']
})
export class SeeNoteComponent implements OnInit, OnDestroy, AfterViewChecked {

  @ViewChild('target') pTagView: ElementRef;
  @ViewChild('commentsTarget') commentsView: ElementRef;

  public id;
  public title;
  public content;
  public created;
  public tags = [];

  noteLoading = true;

  comments: Comment[] = [];
  hasComments = false;

  hasEditPermission = false;
  hasOwnerPermission = false;

  noteSubscription: Subscription;
  commentsSubscription: Subscription;
  setEditPermissionSubscription: Subscription;
  setOwnerPermissionSubscription: Subscription;

  constructor(
    private noteService: NoteService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: SnackbarService,
    private dialog: MatDialog,
  ) {
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');

    this.loadNote();
    this.loadComments();
    this.setEditPermission();
    this.setOwnerPermission();
  }

  ngOnDestroy(): void {
    this.noteSubscription?.unsubscribe();
    this.commentsSubscription?.unsubscribe();
    this.setEditPermissionSubscription?.unsubscribe();
    this.setOwnerPermissionSubscription?.unsubscribe();
  }

  ngAfterViewChecked(): void {
    if (!this.noteLoading && this.hasComments) {
      this.setContentHeight();
    }
  }

  setContentHeight(): void {
    this.commentsView.nativeElement.style.maxHeight = this.pTagView.nativeElement.offsetHeight;
  }

  loadNote(): void {
    this.noteSubscription = this.noteService.getNote(this.id).subscribe(res => {
      this.title = res.title;
      this.content = res.content;
      this.setNoteToEditData();
      if (isNotNullOrUndefined(res.tags)) {
        this.tags = res.tags;
      }

      const timeStamp = res.created.split('T');
      this.created = timeStamp[0];

      this.noteLoading = false;
    }, error => {
      if (error.status === 401) {
        this.snackBar.openSnackbar('No tiene permisos para ver esta nota', 0);
      } else {
        this.snackBar.openSnackbar('¡Ha ocurrido un error, vuelva a intentarlo!', 0);
      }
      this.router.navigate(['/home']);
    });
  }

  private setNoteToEditData(): void {
    this.noteService.saveNoteToEdit({title: this.title, content: this.content});
  }

  openDialog(): void {
    this.dialog.open(ShareNoteDialogComponent, {
      width: '30vw',
      position: {top: '10%'},
      data: {noteId: this.id}
    });
  }

  loadComments(): void {
    this.commentsSubscription = this.noteService.getComments(this.id).subscribe(res => {
      this.hasComments = res.length !== 0;
      if (res.length !== 0) {
        this.comments = this.resolveCommentsData(res);
      }
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
    const commentDate = new Date(date).getTime();
    const dateDifference = (dateNow - commentDate);

    const minutesDifference = Math.floor(dateDifference / minutes);
    const hoursDifference = Math.floor(dateDifference / hours);
    const daysDifference = Math.floor(dateDifference / day);

    if (hoursDifference >= 24) {
      return daysDifference + (daysDifference !== 1 ? ' dias' : ' dia');
    } else if (minutesDifference >= 60) {
      return hoursDifference + (hoursDifference !== 1 ? ' horas' : ' hora');
    } else {
      return minutesDifference + (minutesDifference !== 1 ? ' minutos' : ' minuto');
    }
  }

  setEditPermission(): void {
    this.setEditPermissionSubscription = this.noteService.hasEditPermission(this.id).subscribe(res => {
      this.hasEditPermission = res;
    }, error => {
      console.error(error);
    });
  }

  setOwnerPermission(): void {
    this.setOwnerPermissionSubscription = this.noteService.hasOwnerPermission(this.id).subscribe(res => {
      this.hasOwnerPermission = res;
    }, error => {
      console.error(error);
    });
  }

  startEdit(): void {
    this.router.navigate(['/note/edit/', this.id]);
  }

  changePermissions(): void {
    this.dialog.open(EditNotePermissionsDialogComponent, {
      width: '40%',
      position: {top: '10%'},
      data: {noteId: this.id}
    });
  }
  deleteNote(): void {
    const note = new Note(this.id, this.title);
    const dialogRef = this.dialog.open(DeleteNoteDialogComponent, {
      width: '400px',
      height: '230px',
      data: note,
    });
    dialogRef.afterClosed().subscribe(resNote => {
      if (resNote) {
        this.noteService.deleteNote(resNote).subscribe(resObs => {
          this.router.navigate( ['/home']);
          this.snackBar.openSnackbar('La nota se ha eliminado correctamente!', 0);
        }, error => {
          this.snackBar.openSnackbar('Ha ocurrido un error y la nota no se pudo eliminar.', 0);
        });
      }
    });
  }
}
