import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PlaylistDTO } from '../model/PlaylistDTO';
import { PlaylistDetailsDTO } from '../model/PlaylistDetailsDTO';

@Injectable({
  providedIn: 'root'
})
export class PlaylistService {
  private readonly API = "http://localhost:8080/api/v1/playlist";

  constructor(private httpClient: HttpClient) { }

  findPlaylists(): Observable<PlaylistDTO[]> {
    return this.httpClient.get<PlaylistDTO[]>(`${this.API}`);
  }

  getPlaylistDetails(playlistId: string): Observable<PlaylistDetailsDTO> {
    return this.httpClient.get<PlaylistDetailsDTO>(`${this.API}/${playlistId}`);
  }

  increaseSongPosition(playlistId: string, songId: string): Observable<any> {
    return this.httpClient.patch(`${this.API}/${playlistId}/${songId}/increase`, { });
  }

  decreaseSongPosition(playlistId: string, songId: string): Observable<any> {
    return this.httpClient.patch(`${this.API}/${playlistId}/${songId}/decrease`, { });
  }

  deleteSong(playlistId: string, songId: string): Observable<any> {
    return this.httpClient.delete(`${this.API}/${playlistId}/${songId}`);
  }

  increasePlaylistPosition(playlistId: string): Observable<any> {
    return this.httpClient.patch(`${this.API}/${playlistId}/increase`, { });
  }

  decreasePlaylistPosition(playlistId: string): Observable<any> {
    return this.httpClient.patch(`${this.API}/${playlistId}/decrease`, { });
  }

  deletePlaylist(playlistId: string): Observable<any> {
    return this.httpClient.delete(`${this.API}/${playlistId}`);
  }

  rename(playlistId: string, name: string): Observable<any> {
    return this.httpClient.patch(`${this.API}/rename`, {
      encryptedId: playlistId,
      name: name
    });
  }

  create(playlistName: string): Observable<any> {
    return this.httpClient.post<any>(`${this.API}/create`, {
      name: playlistName
    })
  }
}
