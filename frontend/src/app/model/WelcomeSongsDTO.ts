export interface WelcomeSongsDTO {
    popular: SongDTO[],
    recentReleases: SongDTO[]
}

export interface SongDTO {
    encryptedId: string,
    title: string,
    authors: string[],
    thumbnailPath: string,
    soundLink: string
}