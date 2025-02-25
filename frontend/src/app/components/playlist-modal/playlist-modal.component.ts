import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { SessionService } from '../../services/session.service';
import { PlaylistService } from '../../services/playlist.service';
import { PlaylistDTO } from '../../model/PlaylistDTO';
import { PlaylistWithSongDTO } from '../../model/PlaylistWithSongDTO';

@Component({
  selector: 'app-playlist-modal',
  standalone: false,
  
  templateUrl: './playlist-modal.component.html',
  styleUrl: './playlist-modal.component.css'
})
export class PlaylistModalComponent implements OnInit {
  constructor(private playlistService: PlaylistService,
              private sessionService: SessionService,
              private router: Router,
              private toastrService: ToastrService,
              public dialogRef: MatDialogRef<PlaylistModalComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) { }
           
  playlists: PlaylistWithSongDTO[] = [ ];  

  ngOnInit(): void {
    this.loadPlaylists();
  }

  onClick(playlist: PlaylistWithSongDTO) {
    if(playlist.songIsPresent) {
      this.playlistService
          .deleteSong(playlist.encryptedId, this.data.songId)
          .subscribe({
            next: _res => {
              this.loadPlaylists();
              this.toastrService.info("Song has been deleted successfully from the playlist");
            },
            error: _err => {
              if(_err['Message']) {
                this.toastrService.error(_err['Message'])
              }
            }
          })

    } else {
      this.playlistService
          .addSongToPlaylist(this.data.songId, playlist.encryptedId)
          .subscribe({
            next: _res => {
              this.loadPlaylists();
              this.toastrService.info("Song has been addedd successfully to the playlist");
            },
            error: _err => {
              if(_err['Message']) {
                this.toastrService.error(_err['Message'])
              }
            }
          })
    }
  }

  closeModal(): void {
    this.dialogRef.close();
  }

  private loadPlaylists() {
    this.playlistService
        .findPlaylistsWithSong(this.data.songId)
        .subscribe({
          next: _res => this.playlists = _res,
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })
  }
}
