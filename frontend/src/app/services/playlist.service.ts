import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { PlaylistDTO } from '../model/PlaylistDTO';
import { PlaylistDetailsDTO } from '../model/PlaylistDetailsDTO';
import { PlaylistWithSongDTO } from '../model/PlaylistWithSongDTO';

@Injectable({
  providedIn: 'root'
})
export class PlaylistService {
  private readonly API = "http://localhost:8080/api/v1/playlist";
  constructor(private httpClient: HttpClient) { }

  addSongToPlaylist(songId: string, playlistId: string): Observable<any> {
    return this.httpClient.post<any>(`${this.API}/${playlistId}/${songId}/add`, {
      song: songId,
      playlist: playlistId
    }).pipe(
      catchError(this.handleError)
    );
  }

  findPlaylists(): Observable<PlaylistDTO[]> {
    return this.httpClient.get<PlaylistDTO[]>(`${this.API}`).pipe(
      catchError(this.handleError)
    );
  }

  findPlaylistsWithSong(songId: string): Observable<PlaylistWithSongDTO[]> {
    return this.httpClient.get<PlaylistWithSongDTO[]>(`${this.API}`, { params: { song: songId }}).pipe(
      catchError(this.handleError)
    );
  }

  getPlaylistDetails(playlistId: string): Observable<PlaylistDetailsDTO> {
    return this.httpClient.get<PlaylistDetailsDTO>(`${this.API}/${playlistId}`).pipe(
      catchError(this.handleError)
    );
  }

  increaseSongPosition(playlistId: string, songId: string): Observable<any> {
    return this.httpClient.patch(`${this.API}/${playlistId}/${songId}/increase`, { }).pipe(
      catchError(this.handleError)
    );
  }

  decreaseSongPosition(playlistId: string, songId: string): Observable<any> {
    return this.httpClient.patch(`${this.API}/${playlistId}/${songId}/decrease`, { }).pipe(
      catchError(this.handleError)
    );
  }

  deleteSong(playlistId: string, songId: string): Observable<any> {
    return this.httpClient.delete(`${this.API}/${playlistId}/${songId}`).pipe(
      catchError(this.handleError)
    );
  }

  increasePlaylistPosition(playlistId: string): Observable<any> {
    return this.httpClient.patch(`${this.API}/${playlistId}/increase`, { }).pipe(
      catchError(this.handleError)
    );
  }

  decreasePlaylistPosition(playlistId: string): Observable<any> {
    return this.httpClient.patch(`${this.API}/${playlistId}/decrease`, { }).pipe(
      catchError(this.handleError)
    );
  }

  deletePlaylist(playlistId: string): Observable<any> {
    return this.httpClient.delete(`${this.API}/${playlistId}`).pipe(
      catchError(this.handleError)
    );
  }

  rename(playlistId: string, name: string): Observable<any> {
    return this.httpClient.patch(`${this.API}/rename`, {
      encryptedId: playlistId,
      name: name
    }).pipe(
      catchError(this.handleError)
    );
  }

  create(playlistName: string): Observable<any> {
    return this.httpClient.post<any>(`${this.API}/create`, {
      name: playlistName
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
