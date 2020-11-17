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
      'Para crear una nota se deberá seleccionar sobre el botón "Crear nueva nota". Luego se le podrá colocar un título y se deberá seleccionar el botón "Crear nota".'));
    this.slidesList.push(new TutorialSlide('Ver contenido de una nota', 'assets/img/TutorialStep2.png',
      'Para ingresar al contenido de la nota puede acceder desde el home, apretando sobre la nota que necesite.'));
    this.slidesList.push(new TutorialSlide('Edición de nota', 'assets/img/TutorialStep3.png',
      'Una vez dentro de la nota, tendremos la posibilidad de editarla seleccionando la opción que se encuentra en el extremo derecho, apretando el botón "Editar nota".'));
    this.slidesList.push(new TutorialSlide('Compartir una nota', 'assets/img/TutorialStep4.png',
      'Para poder compartir una nota, deberá seleccionar el botón "Compartir" que se encuentra en el extremo derecho. ' +
      'Luego podrá ingresar el email de la persona a la cual quiere compartir la nota, seleccionar que permisos darle a la misma ' +
      'sobre la nota y para concretar la acción deberá apretar sobre el botón "Compartir" indicado por la flecha.'));
    this.slidesList.push(new TutorialSlide('Edición de permisos de una nota', 'assets/img/TutorialStep5.png',
      'Para poder editar permisos de una nota, deberá seleccionar el botón "Editar permisos" que se encuentra en el extremo derecho. ' +
      'Luego verá los emails de las personas que poseen permisos sobre la nota y en el desplegable situado a la derecha de estos ' +
      'podrá modificar el permiso de esa persona, o también en la cruz puede quitarle los permisos si lo necesita. ' +
      'Para concretar la acción deberá apretar sobre el botón "Confirmar cambios" indicado por la flecha.'));
    this.slidesList.push(new TutorialSlide('Agregar comentarios', 'assets/img/TutorialStep6.png',
      'Para poder agregar comentarios a una nota, deberá introducir su comentario dentro del cuadro de texto que se encuentra en el extremo derecho ' +
      'y luego seleccionar el botón "Agregar comentario." Luego podrá visualizar su comentario justo debajo del cuadro de texto y el botón.'));
    this.slidesList.push(new TutorialSlide('Buscar una nota', 'assets/img/TutorialStep7.png',
      'Para poder buscar una nota, deberá introducir el titulo o las tags que está buscando en el buscador que se encuentra en el home justo debajo del título de la página.'));
    this.slidesList.push(new TutorialSlide('Ver perfil de usuario', 'assets/img/TutorialStep8.png',
      'Para poder ver el perfil de su cuenta, deberá seleccionar los tres puntitos que se encuentran en la esquina ' +
      'superior derecha de la pantalla y luego apretar sobre la opción "Ver Perfil".'));
    this.slidesList.push(new TutorialSlide('Edición de usuario', 'assets/img/TutorialStep9.png',
      'Para poder editar el perfil de su cuenta, deberá ir a la pantalla "Ver Perfil", la cual se indica como hacerlo en el paso anterior. ' +
      'Luego dentro de esta pantalla, tendrá que seleccionar el botón "Editar", de esta manera se habilitará el campo de texto que contiene su nombre y apellido para que pueda realizar la edición. ' +
      'Para concretar con la edición deberá seleccionar el botón "Guardar" posicionado justo debajo de su email. '));

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
