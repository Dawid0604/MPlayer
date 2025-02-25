import { Component, OnInit } from '@angular/core';
import { SessionService } from '../../services/session.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { faArrowRightFromBracket, faBars, faHouse, faMagnifyingGlassLocation, faMusic } from '@fortawesome/free-solid-svg-icons';
import { UserModalComponent } from '../user/user-modal.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-navbar',
  standalone: false,
  
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  isUserLogged: boolean = false;
  homeIcon = faHouse;
  discoverIcon = faMagnifyingGlassLocation;
  playlistIcon = faMusic;
  userIcon = faBars;
  logoutIcon = faArrowRightFromBracket;

  constructor(private sessionService: SessionService,
              private router: Router,
              private toastrService: ToastrService,
              private dialog: MatDialog) { }

  ngOnInit(): void {
    this.sessionService.checkStatus();
    this.sessionService
        .isUserLoggedIn
        .subscribe(_status => {
          this.isUserLogged = _status;
        });
  }

  logout() {
    this.sessionService.logout();
    this.toastrService.info("Success logout");
    this.router.navigate([ '/welcome' ]);
  }

  openUserModal() {
    const dialogRef = this.dialog
                          .open(UserModalComponent);

    dialogRef.afterClosed()
             .subscribe(_result => { });
  }
}
