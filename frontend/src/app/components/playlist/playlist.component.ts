import { Component, OnInit } from '@angular/core';
import { faCircleArrowUp, faCircleCheck, faCircleDown, faCirclePlay, faCircleStop, faCircleUp, faCircleXmark, faClock, faEyeSlash, faFloppyDisk, faMusic, faPenToSquare, faSquareCheck, faSquareXmark, faTrash, faVolumeHigh, faVolumeLow } from '@fortawesome/free-solid-svg-icons';
import { PlaylistService } from '../../services/playlist.service';
import { PlaylistDTO } from '../../model/PlaylistDTO';
import { PlaylistDetailsDTO } from '../../model/PlaylistDetailsDTO';
import { SongDTO } from '../../model/WelcomeSongsDTO';

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

  constructor(private playlistService: PlaylistService) { }

  ngOnInit(): void {
    this.playlistService
        .findPlaylists()
        .subscribe({
          next: _res => {
            this.playlists = _res;
            this.getPlaylistDetails(_res[0].encryptedId)
          },
          error: _err => console.log(_err)
        })
  }

  private reloadPlaylists() {
    this.playlistService
        .findPlaylists()
        .subscribe({
          next: _res => this.playlists = _res,
          error: _err => console.log(_err)
        })
  }

  private getPlaylistDetails(playlistId: string) {
    this.playlistService
        .getPlaylistDetails(playlistId)
        .subscribe({
          next: _sub_res => {
            this.playlistDetails = _sub_res
            this.currentSong = _sub_res.songs[0];
            this.form.text = _sub_res.playlist.name;
          },
          error: _sub_err => console.log(_sub_err)
    })
  }

  private reloadPlaylistSongs() {
    this.playlistService
        .getPlaylistDetails(this.playlistDetails.playlist.encryptedId)
        .subscribe({
          next: _sub_res => this.playlistDetails = _sub_res,
          error: _sub_err => console.log(_sub_err)
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
    this.selectSong(this.selectedSongIndex, this.playlistDetails.songs[0], audioElement);
  }

  increaseSongPosition(songId: string, songPosition: number) {
    this.playlistService
        .increaseSongPosition(this.playlistDetails.playlist.encryptedId, songId)
        .subscribe({
          next: _res => {
            this.reloadPlaylistSongs();
            this.selectedSongIndex = songPosition + 1;
          },
          error: _err => console.log(_err)
        })
  }

  decreaseSongPosition(songId: string, songPosition: number) {
    this.playlistService
        .decreaseSongPosition(this.playlistDetails.playlist.encryptedId, songId)
        .subscribe({
          next: _res => {
            this.reloadPlaylistSongs();
            this.selectedSongIndex = songPosition - 1;
          },
          error: _err => console.log(_err)
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
          error: _err => console.log(_err)
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
          error: _err => console.log(_err)
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
          error: _err => console.log(_err)
        })
  }

  deletePlaylist(playlistId: string) {
    this.playlistService
        .deletePlaylist(playlistId)
        .subscribe({
          next: _res => {
            this.reloadPlaylists();
            this.reloadPlaylistSongs();
          },
          error: _err => console.log(_err)
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
          },
          error: _err => console.log(_err)
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
