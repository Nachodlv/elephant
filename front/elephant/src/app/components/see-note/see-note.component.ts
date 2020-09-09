import {Component, OnInit} from '@angular/core';
import {NoteService} from '../../services/note.service';
import {ActivatedRoute, Router} from '@angular/router';
import {SnackbarService} from '../../services/snackbar.service';

@Component({
  selector: 'app-see-note',
  templateUrl: './see-note.component.html',
  styleUrls: ['./see-note.component.scss']
})
export class SeeNoteComponent implements OnInit {

  public id;
  public title;
  public content;
  public created;

  constructor(
    private noteService: NoteService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: SnackbarService
  ) {
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');

    this.loadNote();
  }

  loadNote(): void {
    this.noteService.getNote(this.id).subscribe(res => {
      this.title = res.title;
      this.content = res.content;

      const timeStamp = res.created.split('T');
      this.created = timeStamp[0];
    }, error => {
      this.snackBar.openSnackbar('¡Ha ocurrido un error, vuelva a intentarlo!', 0);
      this.router.navigate(['/home']);
    });
  }

}
