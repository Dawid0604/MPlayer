import { Component, OnInit } from '@angular/core';
import { SessionService } from '../../services/session.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-navbar',
  standalone: false,
  
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  isUserLogged: boolean = false;

  constructor(private sessionService: SessionService,
              private router: Router,
              private toastrService: ToastrService) { }

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
}
