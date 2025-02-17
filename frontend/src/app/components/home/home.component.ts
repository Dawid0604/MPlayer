import { Component, OnInit } from '@angular/core';
import { faCirclePlay, faCircleStop, faClock, faEyeSlash, faVolumeHigh, faVolumeLow, faVolumeOff } from '@fortawesome/free-solid-svg-icons';
import { SongService } from '../../services/song.service';
import { SongDTO, WelcomeSongsDTO } from '../../model/WelcomeSongsDTO';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-home',
  standalone: false,
  
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  playIcon = faCirclePlay;
  hidePlayerBarIcon = faEyeSlash;
  stopIcon = faCircleStop;
  volumeHighIcon = faVolumeHigh;
  volumeLowIcon = faVolumeLow;
  songTimeIcon = faClock;

  showPlayerBar = false;
  isPlaying = false;
  currentTime = 0;
  volume = 1;

  welcomeSongs: WelcomeSongsDTO = { } as WelcomeSongsDTO;
  currentSong : SongDTO = { } as SongDTO;

  constructor(private songService: SongService,
              private toastrService:ToastrService) { }

  ngOnInit(): void {
    this.songService
        .findWelcomeSongs()
        .subscribe({
          next: _res => {
            if(_res.popular.length > 0 || _res.recentReleases.length > 0) {
              this.welcomeSongs = _res

            } else {
              this.toastrService.warning("Not found songs");
            }
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
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
                          error: _err => {
                            if(_err['Message']) {
                              this.toastrService.error(_err['Message'])
                            }
                          }
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
