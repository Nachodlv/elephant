import {Component, OnDestroy, OnInit} from '@angular/core';
import {NoteService} from '../../services/note.service';
import {ActivatedRoute, Router} from '@angular/router';
import {SnackbarService} from '../../services/snackbar.service';
import {MatDialog} from '@angular/material/dialog';
import {ShareNoteDialogComponent} from '../share-note-dialog/share-note-dialog.component';
import {Subscription} from 'rxjs';

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

  noteSubscription: Subscription;

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
  }

  ngOnDestroy(): void {
    this.noteSubscription?.unsubscribe();
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

}
