import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API = "http://localhost:8080/api/v1/auth";

  constructor(private httpClient: HttpClient) { }

  loginUser(username: string, password: string): Observable<any> {
    return this.httpClient.post<any>(`${this.API}/login`, {
      username: username,
      password: password
    }).pipe(
      catchError(this.handleError)
    );
  }

  registerUser(username: string, password: string,
               nickname: string): Observable<any> {

    return this.httpClient.post<any>(`${this.API}/register`, {
      username: username,
      password: password,
      nickname: nickname
    }).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(errorResponse: HttpErrorResponse) {
    if(errorResponse.error) {
      return throwError(() => errorResponse.error);

    } else {
      return throwError(() => 'Unexpected error');
    }
  }
}
