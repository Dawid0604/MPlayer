import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { faArrowRightToBracket, faUserPlus } from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../../services/auth.service';
import { SessionService } from '../../services/session.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-welcome',
  standalone: false,
  
  templateUrl: './welcome.component.html',
  styleUrl: './welcome.component.css'
})
export class WelcomeComponent {
  registrationIcon = faUserPlus;
  loggingIcon = faArrowRightToBracket;
  loggingFormData = { username: '', password: ''};
  registerFormData = { username: '', password: '', nickname: ''};  

  constructor(private sessionService: SessionService,
              private authService: AuthService,
              private router: Router,
              private toastrService: ToastrService) { }

  loginUser() {
    this.authService
        .loginUser(this.loggingFormData.username, this.loggingFormData.password)
        .subscribe({
          next: _res => {
            this.sessionService.checkStatus();
            this.toastrService.success("Success logging")
            this.router.navigate([ '' ]);            
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.warning(_err['Message'])
            }
          }
        })
  }

  registerUser(form: NgForm) {
    this.authService
        .registerUser(this.registerFormData.username, this.registerFormData.password,
                      this.registerFormData.nickname)
        .subscribe({
          next: _res => {
            form.resetForm();
            this.toastrService.success("Success registration")
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.warning(_err['Message'])
            }
          }
        })
  }
}
