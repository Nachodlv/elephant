import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {Note} from '../../models/note-model';
import {Router} from '@angular/router';

@Component({
  selector: 'app-print-note',
  templateUrl: './print-note.component.html',
  styleUrls: ['./print-note.component.scss']
})
export class PrintNoteComponent implements OnInit, AfterViewInit {

  @Input() noteToPrint: Note;

  constructor(
    private router: Router
  ) {
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.print();
  }

  print(): void {
    const printContents = document.getElementById('printable').innerHTML;
    const originalContents = document.body.innerHTML;

    document.body.innerHTML = printContents;

    window.print();

    document.body.innerHTML = originalContents;

    this.router.navigate(['/home']);
  }

}
