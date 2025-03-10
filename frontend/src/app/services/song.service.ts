import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { WelcomeSongsDTO } from '../model/WelcomeSongsDTO';
import { DiscoverSongsDTO } from '../model/DiscoverSongsDTO';
import { SongGenreDTO } from '../model/SongGenreDTO';
import { SongMoodDTO } from '../model/SongMoodDTO';

@Injectable({
  providedIn: 'root'
})
export class SongService {
  private readonly API = "http://localhost:8080/api/v1/song";
  constructor(private httpClient: HttpClient) { }

  findWelcomeSongs(): Observable<WelcomeSongsDTO> {
    return this.httpClient.get<WelcomeSongsDTO>(`${this.API}/find/welcome`).pipe(
      catchError(this.handleError)
    );
  }

  findGenres(): Observable<SongGenreDTO[]> {
    return this.httpClient.get<SongGenreDTO[]>(`${this.API}/genres`).pipe(
      catchError(this.handleError)
    );
  }
  
  findMoods(): Observable<SongMoodDTO[]> {
    return this.httpClient.get<SongMoodDTO[]>(`${this.API}/moods`).pipe(
      catchError(this.handleError)
    );
  }

  handleSongListening(songId: string): Observable<any> {
    return this.httpClient.patch<any>(`${this.API}/listening/${songId}/handle`, { }).pipe(
      catchError(this.handleError)
    );
  }

  discoverSongs(pageNumber: number, searchedText: string,
                genres: string[], moods: string[]): Observable<DiscoverSongsDTO> {

    return this.httpClient.post<DiscoverSongsDTO>(`${this.API}/discover`, { 
      searchedText: searchedText,
      genres: genres,
      moods: moods,
      pageNumber: pageNumber,
      pageSize: 8
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
