import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { PlaylistComponent } from './components/playlist/playlist.component';
import { DiscoverComponent } from './components/discover/discover.component';

const routes: Routes = [
  {
    path: "",
    component: HomeComponent
  },
  {
    path: "playlist",
    component: PlaylistComponent
  },
  {
    path: "discover",
    component: DiscoverComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
