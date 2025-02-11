import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private $loggedInObservable = new BehaviorSubject<boolean>(false);
  public isUserLoggedIn = this.$loggedInObservable.asObservable();

  constructor(private cookieService: CookieService) { }
  
  public checkStatus() {    
      this.$loggedInObservable.next(this.cookieService.check("JSESSIONID"));
  }

  public logout() {
    this.cookieService.delete("JSESSIONID");
    this.$loggedInObservable.next(false);
  }
}
