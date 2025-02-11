import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpInterceptorFn, HttpRequest, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, Observable, throwError } from 'rxjs';
import { SessionService } from '../services/session.service';

@Injectable()
export class RequestInterceptor implements HttpInterceptor {

  constructor(private router: Router, 
              private sessionService: SessionService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    req = req.clone({ withCredentials: true });
    return next.handle(req)
               .pipe(
                  catchError((_error: HttpErrorResponse) => {
                    if(_error.status === 401 || _error.status === 403) {
                      this.handleUnauthorized();
                    } return throwError(() => _error);
                  })
               );
  }

  private handleUnauthorized() {
    this.sessionService.logout();
    this.router.navigate([ '/welcome' ]);
  }
}