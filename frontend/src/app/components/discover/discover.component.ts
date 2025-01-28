import { Component, OnInit } from '@angular/core';
import { faCirclePlay, faCirclePlus, faCircleStop, faClock, faEyeSlash, faVolumeHigh, faVolumeLow } from '@fortawesome/free-solid-svg-icons'
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons'
import { DiscoverSongsDTO, SongDTO } from '../../model/DiscoverSongsDTO';
import { SongService } from '../../services/song.service';
import { SongMoodDTO } from '../../model/SongMoodDTO';
import { SongGenreDTO } from '../../model/SongGenreDTO';

@Component({
  selector: 'app-discover',
  standalone: false,
  
  templateUrl: './discover.component.html',
  styleUrl: './discover.component.css'
})
export class DiscoverComponent implements OnInit {
  playIcon = faCirclePlay;
  playlistIcon = faCirclePlus;
  searchIcon = faMagnifyingGlass;  
  hidePlayerBarIcon = faEyeSlash;
  stopIcon = faCircleStop;
  volumeHighIcon = faVolumeHigh;
  volumeLowIcon = faVolumeLow;
  songTimeIcon = faClock;

  showPlayerBar = false;
  isPlaying = false;
  currentTime = 0;
  volume = 1;

  currentSong: SongDTO = { } as SongDTO;  
  discoveredSongs: DiscoverSongsDTO = { } as DiscoverSongsDTO;
  songMoods: SongMoodDTO[] = [ ];
  songGenres: SongGenreDTO[] = [ ];
  pageNumber = 1;
 
  constructor(private songService: SongService) { }
 
  ngOnInit(): void {
    this.songService
        .discoverSongs()
        .subscribe({
          next: _res => this.discoveredSongs = _res,
          error: _err => console.log(_err)
        })

    this.songService
        .findGenres()
        .subscribe({
          next: _res => this.songGenres = _res,
          error: _err => console.log(_err)
        })

    this.songService
        .findMoods()
        .subscribe({
          next: _res => this.songMoods = _res,
          error: _err => console.log(_err)
    })
  }
 
  selectSong(song: SongDTO, audioElement: HTMLAudioElement) {
    this.currentSong = song;
    this.showPlayerBar = true;
    this.isPlaying = false;
    this.currentTime = 0;
    this.volume = 1;    
    audioElement.load();
  }
 
  hidePlayer(audioElement: HTMLAudioElement) {
    this.currentSong = { } as SongDTO;
    this.showPlayerBar = false;
    this.isPlaying = false;
    this.currentTime = 0;
    this.volume = 1;
    
    audioElement.pause();    
    audioElement.currentTime = 0;
  }
 
  togglePlayPause(audioElement: HTMLAudioElement) {    
    if (this.isPlaying) {
        audioElement.pause();
 
    } else {
        if(this.currentTime === 0) {
          audioElement.load();
        }
  
        audioElement.play();
        this.songService.handleSongListening(this.currentSong.encryptedId)
                        .subscribe({
                          next: _res => { },
                          error: _err => console.log(_err)
                        })
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
