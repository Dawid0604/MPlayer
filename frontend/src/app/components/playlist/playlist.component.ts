import { Component, OnInit } from '@angular/core';
import { faCircleArrowUp, faCircleCheck, faCircleDown, faCirclePlay, faCircleStop, faCircleUp, faCircleXmark, faClock, faEyeSlash, faFloppyDisk, faMusic, faPenToSquare, faPlusCircle, faSquareCheck, faSquareXmark, faTrash, faVolumeHigh, faVolumeLow } from '@fortawesome/free-solid-svg-icons';
import { PlaylistService } from '../../services/playlist.service';
import { PlaylistDTO } from '../../model/PlaylistDTO';
import { PlaylistDetailsDTO } from '../../model/PlaylistDetailsDTO';
import { SongDTO } from '../../model/WelcomeSongsDTO';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-playlist',
  standalone: false,
  
  templateUrl: './playlist.component.html',
  styleUrl: './playlist.component.css'
})
export class PlaylistComponent implements OnInit {  
  playIcon = faCirclePlay;  
  stopIcon = faCircleStop;
  volumeHighIcon = faVolumeHigh;
  volumeLowIcon = faVolumeLow;
  songTimeIcon = faClock;
  songIcon = faMusic;
  upIcon = faCircleArrowUp;
  downIcon = faCircleDown;
  deletePlaylistIcon = faTrash;
  updatePlaylistIcon = faFloppyDisk;
  editPlaylistIcon = faPenToSquare;
  savePlaylistIcon = faCircleCheck;
  cancelPlaylistIcon = faCircleXmark;
  newPlaylistIcon = faPlusCircle;  

  showCreatePlaylistBox = false;
  isPlaying = false;
  currentTime = 0;
  volume = 1;
  
  playlists: PlaylistDTO[] = [ ];
  currentSong : SongDTO = { } as SongDTO;
  currentPlaylist: PlaylistDTO = { } as PlaylistDTO;
  playlistDetails: PlaylistDetailsDTO = { } as PlaylistDetailsDTO;
  selectedSongIndex: number = 0;
  selectedPlaylistIndex: number = 0;
  selectedPlaylistToEditIndex: number = 0;
  editPlaylistMode: boolean = false;
  form: any = { };
  creatingPlaylistFormData = { name: '' };

  constructor(private playlistService: PlaylistService,
              private toastrService: ToastrService) { }

  ngOnInit(): void {
    this.playlistService
        .findPlaylists()
        .subscribe({
          next: _res => {
            this.playlists = _res;

            if(_res[0]) {
              this.getPlaylistDetails(_res[0].encryptedId)
            }
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })
  }

  private reloadPlaylists() {
    this.playlistService
        .findPlaylists()
        .subscribe({
          next: _res => this.playlists = _res,
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })
  }

  private getPlaylistDetails(playlistId: string) {
    this.playlistService
        .getPlaylistDetails(playlistId)
        .subscribe({
          next: _res => {
            this.playlistDetails = _res
            this.form.text = _res.playlist.name;

            if(_res.songs.length === 0) {
              this.currentSong = { } as SongDTO;

            } else {
              this.currentSong = _res.songs[0];
            }
            
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
    })
  }

  private reloadPlaylistSongs() {
    this.playlistService
        .getPlaylistDetails(this.playlistDetails.playlist.encryptedId)
        .subscribe({
          next: _res => this.playlistDetails = _res,
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
    })
  }
  
  playNextSong(audioElement: HTMLAudioElement) {
    if(this.selectedSongIndex + 1 <= this.playlistDetails.songs.length - 1) {      
      this.selectSong(this.selectedSongIndex += 1, this.playlistDetails.songs[this.selectedSongIndex], audioElement);

    } else {
      this.selectedSongIndex = 0;
      const firstSong = this.playlistDetails.songs[0];
      this.selectSong(this.selectedSongIndex, firstSong, audioElement);
    }
  }

  selectSong(songPosition: number, song: SongDTO, audioElement: HTMLAudioElement) {
    this.selectedSongIndex = songPosition;
    this.currentSong = song;    
    this.isPlaying = true;
    this.currentTime = 0;    

    setTimeout(() => {
      audioElement.load();
      audioElement.play();
    }, 250)
  }

  selectPlaylist(playlistPosition: number, playlist: PlaylistDTO,
                 audioElement: HTMLAudioElement) {

    this.selectedPlaylistIndex = playlistPosition;
    this.currentPlaylist = playlist;
    this.getPlaylistDetails(playlist.encryptedId);
    
    this.form.text = playlist.name;
    this.selectedSongIndex = 0;
    this.selectedSongIndex = this.selectedSongIndex;
    this.currentSong = this.playlistDetails.songs[0];    
    this.isPlaying = false;
    this.currentTime = 0;   
    audioElement.load();
  }

  increaseSongPosition(songId: string, songPosition: number) {
    this.playlistService
        .increaseSongPosition(this.playlistDetails.playlist.encryptedId, songId)
        .subscribe({
          next: _res => {
            this.reloadPlaylistSongs();

            if(this.currentSong.encryptedId === songId) {
              this.selectedSongIndex = songPosition + 1;
            }
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })
  }

  decreaseSongPosition(songId: string, songPosition: number) {
    this.playlistService
        .decreaseSongPosition(this.playlistDetails.playlist.encryptedId, songId)
        .subscribe({
          next: _res => {
            this.reloadPlaylistSongs();

            if(this.currentSong.encryptedId === songId) {
              this.selectedSongIndex = songPosition - 1;
            }
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })
  }

  deleteSong(songId: string) {
    this.playlistService
        .deleteSong(this.playlistDetails.playlist.encryptedId, songId)
        .subscribe({
          next: _res => {
            this.reloadPlaylists();
            this.reloadPlaylistSongs();                        
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })
  }

  increasePlaylistPosition(playlistId: string, playlistPosition: number) {
    this.playlistService
        .increasePlaylistPosition(playlistId)
        .subscribe({
          next: _res => {
            this.reloadPlaylists();
            this.reloadPlaylistSongs();
            this.selectedPlaylistIndex = playlistPosition + 1;
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })
  }

  decreasePlaylistPosition(playlistId: string, playlistPosition: number) {
    this.playlistService
        .decreasePlaylistPosition(playlistId)
        .subscribe({
          next: _res => {
            this.reloadPlaylists();
            this.reloadPlaylistSongs();
            this.selectedPlaylistIndex = playlistPosition - 1;
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })
  }

  deletePlaylist(playlistId: string) {
    this.playlistService
        .deletePlaylist(playlistId)
        .subscribe({
          next: _res => {
            this.selectedPlaylistIndex = 0;
            this.playlistService
                .findPlaylists()
                .subscribe({
                  next: _res => {
                    this.playlists = _res
            
                    if(this.playlists && this.playlists.length >= 1) {              
                      this.getPlaylistDetails(this.playlists[0].encryptedId);
                    
                    } else {              
                      this.playlistDetails = { } as PlaylistDetailsDTO;
                      this.currentSong = { } as SongDTO;
                      this.currentTime = 0;
                    }
                  
                    this.toastrService.info("Playlist has been deleted")
                  },
                  error: _err => {
                    if(_err['Message']) {
                      this.toastrService.error(_err['Message'])
                    }
                  }
                })
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })
  }

  editPlaylist(playlist: PlaylistDTO, playlistPosition: number) {
    this.editPlaylistMode = !this.editPlaylistMode;
    this.selectedPlaylistToEditIndex = playlistPosition;
    this.form.text = playlist.name;
  }

  renamePlaylist(playlistId: string) {
    this.playlistService
        .rename(playlistId, this.form.text)
        .subscribe({
          next: _res => {
            this.reloadPlaylists();
            this.editPlaylistMode = false;
            this.toastrService.info("Playlist has been renamed");
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })
  }

  createPlaylist() {
    this.playlistService
        .create(this.creatingPlaylistFormData.name)
        .subscribe({
          next: _res => {
            this.reloadPlaylists();
            this.creatingPlaylistFormData.name = '';
            this.showCreatePlaylistBox = false;
            this.toastrService.info("Playlist has been created")
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })
  }

  togglePlayPause(audioElement: HTMLAudioElement) {
    if (this.isPlaying) {
        audioElement.pause();

    } else {
      if(this.currentTime === 0) {
        audioElement.load();
      }
      
        audioElement.play();
    }
    this.isPlaying = !this.isPlaying;
  }

  onTimeUpdate(audioElement: HTMLAudioElement) {
    this.currentTime = audioElement.currentTime;
  }

  seekAudio(audioElement: HTMLAudioElement, event: any) {
    audioElement.currentTime = event.target.value;
  }

  formatTime(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = Math.floor(seconds % 60);
    return `${minutes}:${remainingSeconds < 10 ? '0' : ''}${remainingSeconds}`;
  }

  adjustVolume(audioElement: HTMLAudioElement) {
    audioElement.volume = this.volume;
  }
}
