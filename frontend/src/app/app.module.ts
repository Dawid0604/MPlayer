import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { FormsModule } from '@angular/forms';
import { PlaylistComponent } from './components/playlist/playlist.component';
import { DiscoverComponent } from './components/discover/discover.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { RequestInterceptor } from './interceptor/request.interceptor';
import { CookieService } from 'ngx-cookie-service';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { ToastrModule, ToastNoAnimation, ToastNoAnimationModule } from 'ngx-toastr';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    NavbarComponent,
    PlaylistComponent,
    DiscoverComponent,
    WelcomeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    FontAwesomeModule,
    ToastNoAnimationModule.forRoot({
      positionClass: 'toast-bottom-right',
      preventDuplicates: true
    })
  ],
  providers: [
    CookieService,
    provideHttpClient(withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: RequestInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
