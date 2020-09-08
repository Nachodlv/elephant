import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, FormGroupDirective, Validators} from '@angular/forms';
import {Subscription} from 'rxjs';
import {NoteService} from '../../services/note.service';
import {SnackbarService} from '../../services/snackbar.service';
import {MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-share-note-dialog',
  templateUrl: './share-note-dialog.component.html',
  styleUrls: ['./share-note-dialog.component.scss']
})
export class ShareNoteDialogComponent implements OnInit, OnDestroy {

  @ViewChild('form') form: FormGroupDirective;
  shareForm: FormGroup;
  shareSubscription: Subscription;

  defaultMode: string;

  constructor(
    private formBuilder: FormBuilder,
    private noteService: NoteService,
    private snackBar: SnackbarService,
    public dialogRef: MatDialogRef<ShareNoteDialogComponent>,
  ) { }

  ngOnInit(): void {
    this.defaultMode = 'read';

    this.shareForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      mode: ['', Validators.required]
    });
  }
  ngOnDestroy(): void {
    this.shareSubscription?.unsubscribe();
  }

  onSubmit(): void {
    this.shareSubscription = this.noteService.shareNote(this.shareForm.value).subscribe(res => {
      this.snackBar.openSnackbar('¡Se ha compartido con exito!', 0);
      this.dialogRef.close();
    }, error => {
      this.snackBar.openSnackbar('¡Ha ocurrido un error, vuelva a intentarlo!', 0);
    });
  }
}
