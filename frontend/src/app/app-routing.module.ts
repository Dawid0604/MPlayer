import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { PlaylistComponent } from './components/playlist/playlist.component';
import { DiscoverComponent } from './components/discover/discover.component';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { AuthGuard } from './guard/auth.guard';

const routes: Routes = [
  {
    path: "",
    component: HomeComponent,
    canActivate: [ AuthGuard ]
  },
  {
    path: "playlist",
    component: PlaylistComponent,
    canActivate: [ AuthGuard ]
  },
  {
    path: "discover",
    component: DiscoverComponent,
    canActivate: [ AuthGuard ]
  },
  {
    path: "welcome",
    component: WelcomeComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
