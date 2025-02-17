import { Component, OnInit } from '@angular/core';
import { faCirclePlay, faCirclePlus, faCircleStop, faClock, faEraser, faEyeSlash, faVolumeHigh, faVolumeLow } from '@fortawesome/free-solid-svg-icons'
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons'
import { DiscoverSongsDTO, Pageable, SongDTO } from '../../model/DiscoverSongsDTO';
import { SongService } from '../../services/song.service';
import { SongMoodDTO } from '../../model/SongMoodDTO';
import { SongGenreDTO } from '../../model/SongGenreDTO';
import { ToastrService } from 'ngx-toastr';

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
  resetIcon = faEraser

  showPlayerBar = false;
  isPlaying = false;
  currentTime = 0;
  volume = 1;
  
  currentSong: SongDTO = { } as SongDTO;  
  discoveredSongs: DiscoverSongsDTO = { 
    songs: [ ] as SongDTO[],
    pageable: { } as Pageable
  } as DiscoverSongsDTO;
  
  songMoods: SongMoodDTO[] = [ ];
  songGenres: SongGenreDTO[] = [ ];
  selectedSongMoods: string[] = [ ];
  selectedSongGenres: string[] = [ ];
  searchedText: string = "";
  form: any = { };
 
  constructor(private songService: SongService,
              private toastrService: ToastrService) { }
 
  ngOnInit(): void {
    this.songService
        .discoverSongs(0, this.searchedText, this.selectedSongGenres, this.selectedSongMoods)
        .subscribe({
          next: _res => {
            if(_res.songs.length > 0) {
              this.discoveredSongs = _res;

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

    this.songService
        .findGenres()
        .subscribe({
          next: _res => {
            if(_res.length > 0) {
              this.songGenres = _res

            } else {
              this.toastrService.warning("Not found genres");
            }
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })

    this.songService
        .findMoods()
        .subscribe({
          next: _res => {
            if(_res.length > 0) {
              this.songMoods = _res

            } else {
              this.toastrService.warning("Not found moods");
            }
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
    })
  }

  navigate(pageNumber: number) {    
    this.songService
        .discoverSongs(pageNumber, this.searchedText, this.selectedSongGenres, this.selectedSongMoods)
        .subscribe({
          next: _res => {
            if(_res.songs.length > 0) {
              this.discoveredSongs = _res

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

  getNextPage(): number {
    const currentPage = this.discoveredSongs.pageable.pageNumber;
    return (currentPage + 1) < this.discoveredSongs.pageable.totalPages ? currentPage + 1 : 0;
  }

  getPreviousPage(): number {
    const currentPage = this.discoveredSongs.pageable.pageNumber;
    return (currentPage - 1) > 0 ? currentPage - 1 : 0;
  }

  selectGenre(genreId: string) {        
    if(genreId) {          
      if(!this.selectedSongGenres.includes(genreId)) {
        this.selectedSongGenres.push(genreId);             

      } else {
        this.selectedSongGenres = this.selectedSongGenres.filter(_genreId => _genreId !== genreId);
      }    
      
      this.discoverSongs();
    }
  }
  
  selectMood(moodId: string) {
    if(moodId) {
      if(!this.selectedSongMoods.includes(moodId)) {
        this.selectedSongMoods.push(moodId);          

      } else {
        this.selectedSongMoods = this.selectedSongMoods.filter(_moodId => _moodId !== moodId);
      }

      this.discoverSongs();
    }
  }

  resetSelections() {
    this.selectedSongGenres = [ ];
    this.selectedSongMoods = [ ];
    this.form.text = '';
    this.searchedText = '';    
    this.discoverSongs();
  }

  discoverByText() {
    if(this.form.text && this.form.text.length >= 3) {
      this.searchedText = this.form.text;
      this.discoverSongs();
      this.form.text = "";
    }
  }

  private discoverSongs() {
    this.songService
        .discoverSongs(this.discoveredSongs.pageable.pageNumber, this.searchedText, this.selectedSongGenres, this.selectedSongMoods)
        .subscribe({
          next: _res => {
            if(_res.songs.length > 0) {
              this.discoveredSongs = _res;

            } else {
              this.toastrService.warning("Not founs songs");
            }
          },
          error: _err => {
            if(_err['Message']) {
              this.toastrService.error(_err['Message'])
            }
          }
        })
  }

  showPlaylists() {

  }

  addSongToPlaylist(song: SongDTO, playlistId: string) {

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
