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
    this.slidesList.push(new TutorialSlide('Creación de nota', 'assets/img/TutorialStep1.png',
      'Para crear una nota se deberá seleccionar sobre el botón "Crear nueva nota". Luego se le podrá colocar un título y se deberá seleccionar el botón "Crear nota"'));
    this.slidesList.push(new TutorialSlide('Ver contenido de una nota', 'assets/img/TutorialStep2.png',
      'Para ingresar al contenido de la nota puede acceder desde el home, aprentando sobre la nota que necesite'));
    this.slidesList.push(new TutorialSlide('Edición de nota', 'assets/img/TutorialStep3.png',
      'Una vez dentro de la nota, tendremos la posibilidad de editarla seleccionando la opción que se encuentra en el extremo derecho, apretando el boton "Editar nota"'));
    this.slidesList.push(new TutorialSlide('Compartir una nota', 'assets/img/TutorialStep4.png',
      'Para poder compartir una nota, deberá seleccionar el boton "Compartir" que se encuentra en el extremo derecho. ' +
      'Luego podrá ingresar el email de la persona a la cual quiere compartir la nota, seleccionar que permisos darle a la misma ' +
      'sobre la nota y para concretar la acción deberá apretar sobre el boton "Compartir" indicado por la flecha'));
    this.slidesList.push(new TutorialSlide('Edición de permisos de una nota', 'assets/img/TutorialStep2.png', ''));
    this.slidesList.push(new TutorialSlide('Agregar comentarios', 'assets/img/TutorialStep2.png', ''));
    this.slidesList.push(new TutorialSlide('Buscar una nota', 'assets/img/TutorialStep2.png', ''));
    this.slidesList.push(new TutorialSlide('Ver perfil de usuario', 'assets/img/TutorialStep2.png', ''));
    this.slidesList.push(new TutorialSlide('Edición de usuario', 'assets/img/TutorialStep2.png', ''));

    this.titleToShow = this.slidesList[0].title;
    this.descriptionToShow = this.slidesList[0].description;
  }

  onClose(): void {
    this.dialog.open(AlertDialogComponent, {
      width: '22%',
      height: '18%',
      data: {
        message: '¿Está seguro que quiere saltearse el tutorial?',
        parentDialog: this.dialogRef
      }
    });
  }

  onFinish(): void {
    this.dialog.open(AlertDialogComponent, {
      width: '22%',
      height: '18%',
      data: {
        message: '¿Está seguro que desea terminar el tutorial?',
        parentDialog: this.dialogRef
      }
    });
  }

  onChange(index): void {
    this.titleToShow = this.slidesList[index].title;
    this.descriptionToShow = this.slidesList[index].description;
  }

  onPrevious(carousel): void {
    carousel.previous();
  }

  onNext(carousel): void {
    carousel.next();
  }
}
