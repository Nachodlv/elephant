import {Component, OnInit} from '@angular/core';
import {TutorialSlide} from '../../models/tutorial-slide-model';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {AlertDialogComponent} from '../alert-dialog/alert-dialog.component';

@Component({
  selector: 'app-tutorial-dialog',
  templateUrl: './tutorial-dialog.component.html',
  styleUrls: ['./tutorial-dialog.component.scss']
})
export class TutorialDialogComponent implements OnInit {

  slidesList: TutorialSlide[] = [];
  titleToShow: string;
  descriptionToShow: string;

  constructor(
    public dialogRef: MatDialogRef<TutorialDialogComponent>,
    private dialog: MatDialog,
  ) {
  }

  ngOnInit(): void {
    this.loadSlides();
  }

  loadSlides(): void {
    this.slidesList.push(new TutorialSlide('Creación de nota', 'assets/img/TutorialStep1.png', 'Para crear una nota se deberá seleccionar sobre el botón "Crear nueva nota". Luego se le podrá colocar un título y se deberá seleccionar el botón "Crear nota"'));
    this.slidesList.push(new TutorialSlide('Ver contenido de una nota', 'assets/img/TutorialStep2.png', 'Para ingresar al contenido de la nota puede acceder desde el home, aprentando sobre la nota que necesite'));

    this.titleToShow = this.slidesList[0].title;
    this.descriptionToShow = this.slidesList[0].description;
  }

  onClose(): void {
    this.dialog.open(AlertDialogComponent, {
      width: '22%',
      height: '18%',
      data: '¿Está seguro que quiere saltearse el tutorial?'
    });
  }

  onChange(index): void {
    this.descriptionToShow = this.slidesList[index].description;
  }

}
