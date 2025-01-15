import { Component } from '@angular/core';
import { faCircleArrowUp, faCircleDown, faCirclePlay, faCircleStop, faCircleUp, faClock, faEyeSlash, faFloppyDisk, faMusic, faPenToSquare, faTrash, faVolumeHigh, faVolumeLow } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-playlist',
  standalone: false,
  
  templateUrl: './playlist.component.html',
  styleUrl: './playlist.component.css'
})
export class PlaylistComponent {
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

  isPlaying = false;
  currentTime = 0;
  volume = 1;
  currentTrack = { title: "Jim Yosef - Link", url: 'https://ncsmusic.s3.eu-west-1.amazonaws.com/tracks/000/000/356/link-1586950573-1rqR7whieT.mp3' };

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
