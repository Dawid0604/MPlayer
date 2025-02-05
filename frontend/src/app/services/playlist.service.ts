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
}
