import { Component } from '@angular/core';
import { faCirclePlay, faCirclePlus, faCircleStop, faClock, faEyeSlash, faVolumeHigh, faVolumeLow } from '@fortawesome/free-solid-svg-icons'
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons'

@Component({
  selector: 'app-discover',
  standalone: false,
  
  templateUrl: './discover.component.html',
  styleUrl: './discover.component.css'
})
export class DiscoverComponent {
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

  currentTrack = { url: 'https://ncsmusic.s3.eu-west-1.amazonaws.com/tracks/000/000/936/royalty-1619082033-7RC2AlRdd1.mp3', title: 'Alan Walker - Fade', img: 'https://linkstorage.linkfire.com/medialinks/images/374fc4ba-fe39-4bcf-9cf0-74c87c879ed0/artwork-440x440.jpg' };

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
