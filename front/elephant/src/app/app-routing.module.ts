import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {ExampleGuard} from './guards/example.guard';
import {HomeComponent} from './components/home/home.component';
import {NoteCreatorComponent} from "./components/note-creator/note-creator.component";
import {NoteComponent} from "./components/note/note.component";


const routes: Routes = [
  {
    path: 'home', component: HomeComponent
    //, canActivate: [ExampleGuard]
  },
  {path: 'note/create', component: NoteCreatorComponent},
  {path: 'note', component: NoteComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
