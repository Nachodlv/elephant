import { Component, OnInit } from '@angular/core';
import {NoteService} from '../../services/note.service';
import {ActivatedRoute} from '@angular/router';

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

  constructor(private noteService: NoteService, private route: ActivatedRoute) { }

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
    });
  }

}
