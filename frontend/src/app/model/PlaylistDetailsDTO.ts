import { PlaylistDTO } from "./PlaylistDTO";
import { SongDTO } from "./WelcomeSongsDTO";

export interface PlaylistDetailsDTO {
    playlist: PlaylistDTO,
    songs: SongDTO[]
}