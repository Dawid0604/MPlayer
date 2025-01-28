import { SongGenreDTO } from "./SongGenreDTO"
import { SongMoodDTO } from "./SongMoodDTO"

export interface DiscoverSongsDTO {
    pageable: Pageable,
    songs: SongDTO[]
}

export interface Pageable {
    pageNumber: number,
    pageSize: number,
    totalPages: number,
    totalElements: number,
    hasPrevious: boolean,
    hasNext: boolean,
    first: boolean,
    last:boolean
}

export interface SongDTO {
    encryptedId: string,
    title: string,
    authors: string[],
    genres: SongGenreDTO[],
    moods: SongMoodDTO[],
    thumbnailPath: string,
    releaseDate: string,
    soundLink: string
}