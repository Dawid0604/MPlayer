import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UserService } from '../../services/user.service';
import { UserDataDTO } from '../../model/UserDataDTO';
import { ToastrService } from 'ngx-toastr';
import { SessionService } from '../../services/session.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user',
  standalone: false,
  
  templateUrl: './user-modal.component.html',
  styleUrl: './user-modal.component.css'
})
export class UserModalComponent implements OnInit {
  constructor(private userService: UserService,
              private sessionService: SessionService,
              private router: Router,
              private toastrService: ToastrService,
              public dialogRef: MatDialogRef<UserModalComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) { }

  loggedUserData: UserDataDTO = { } as UserDataDTO;
  newPassword: string = '';

  ngOnInit(): void {
    this.userService
        .getLoggedUserData()
        .subscribe({
          next: _res => this.loggedUserData = _res,
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })
  }

  deleteUser(): void {
    this.userService
        .delete()
        .subscribe({
          next: _res => {
            this.closeModal();
            this.toastrService.info("User has been deleted successfully!");            
            this.sessionService.logout();
            this.toastrService.info("Success logout");
            this.router.navigate([ '/welcome' ]);
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })
  }

  updatePassword(): void {
    if(this.newPassword && this.newPassword.length < 6) {
      this.toastrService.warning("Password must be greater than 6 characters"); return;
    }

    this.userService
        .updatePassword(this.newPassword)
        .subscribe({
          next: _res => {
            this.toastrService.success("Password changed successfully!");
            this.closeModal();
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })
  }
              
  closeModal(): void {
    this.dialogRef.close();
  }
}
