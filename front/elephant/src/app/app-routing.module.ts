import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ExampleGuard} from './guards/example.guard';
import {HomeComponent} from './components/home/home.component';
import {RegisterComponent} from "./components/register/register.component";


const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'home', component: HomeComponent, canActivate: [ExampleGuard] }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
