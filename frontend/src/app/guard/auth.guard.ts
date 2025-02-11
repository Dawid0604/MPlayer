import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanActivateFn, GuardResult, MaybeAsync, Router, RouterStateSnapshot } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router,
              private cookieService: CookieService) { }

  canActivate(_route: ActivatedRouteSnapshot, _state: RouterStateSnapshot): MaybeAsync<GuardResult> {
    if(this.cookieService.check('JSESSIONID')) {
      return true;
    }

    this.router.navigate([ '/welcome' ]);
    return false;
  }
}