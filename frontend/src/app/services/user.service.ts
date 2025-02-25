import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { UserDataDTO } from '../model/UserDataDTO';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly API = "http://localhost:8080/api/v1/user";
  constructor(private httpClient: HttpClient) { }

  delete(): Observable<any> {
    return this.httpClient.delete<any>(`${this.API}`).pipe(
      catchError(this.handleError)
    );
  }

  getLoggedUserData(): Observable<UserDataDTO> {
    return this.httpClient.get<UserDataDTO>(`${this.API}`).pipe(
      catchError(this.handleError)
    )
  }

  updatePassword(password: string): Observable<any> {
    return this.httpClient.patch<any>(`${this.API}`, { password: password }).pipe(
      catchError(this.handleError)
    )
  }

  private handleError(errorResponse: HttpErrorResponse) {
    if(errorResponse.error) {
      return throwError(() => errorResponse.error);

    } else {
      return throwError(() => 'Unexpected error');
    }
  }
}
