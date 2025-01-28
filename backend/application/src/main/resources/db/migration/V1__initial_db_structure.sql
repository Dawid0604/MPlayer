CREATE TABLE IF NOT EXISTS SPRING_SESSION (
  PRIMARY_ID char(36) NOT NULL,
  SESSION_ID char(36) NOT NULL,
  CREATION_TIME bigint(20) NOT NULL,
  LAST_ACCESS_TIME bigint(20) NOT NULL,
  MAX_INACTIVE_INTERVAL int(11) NOT NULL,
  EXPIRY_TIME bigint(20) NOT NULL,
  PRINCIPAL_NAME varchar(100) NULL,

  PRIMARY KEY (PRIMARY_ID),
  UNIQUE KEY SPRING_SESSION_IX1 (SESSION_ID),
  KEY SPRING_SESSION_IX2 (EXPIRY_TIME),
  KEY SPRING_SESSION_IX3 (PRINCIPAL_NAME)
);

CREATE TABLE IF NOT EXISTS SPRING_SESSION_ATTRIBUTES (
  SESSION_PRIMARY_ID char(36) NOT NULL,
  ATTRIBUTE_NAME varchar(200) NOT NULL,
  ATTRIBUTE_BYTES blob NOT NULL,

  PRIMARY KEY (SESSION_PRIMARY_ID,ATTRIBUTE_NAME)
);

CREATE TABLE IF NOT EXISTS UserRoles(
    Id BIGINT AUTO_INCREMENT NOT NULL,
    EncryptedId varchar(256) NULL,
    Name varchar(64) NOT NULL,

    PRIMARY KEY (Id),
    UNIQUE KEY user_roles_encryptedId_unique (EncryptedId),
    UNIQUE KEY user_roles_name_unique (Name)
);

CREATE TABLE IF NOT EXISTS Users(
    Id BIGINT AUTO_INCREMENT NOT NULL,
    EncryptedId varchar(256) NULL,
    Username varchar(64) NULL,
    Password varchar(64) NULL,
    Nickname varchar(32) NULL,
    CreatedDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    RoleId BIGINT NOT NULL,

    PRIMARY KEY (Id),
    CONSTRAINT users_fk_role FOREIGN KEY (RoleId) REFERENCES UserRoles(Id),
    UNIQUE KEY users_username_unique (Username),
    UNIQUE KEY users_encryptedId_unique (EncryptedId),
    UNIQUE KEY users_nickname_unique (Nickname),
    KEY users_username_idx (Username)
);

CREATE TABLE IF NOT EXISTS SongGenres(
    Id BIGINT AUTO_INCREMENT NOT NULL,
    EncryptedId varchar(256) NULL,
    Name varchar(32) NOT NULL,
    Color varchar(7),

    PRIMARY KEY (Id),
    UNIQUE KEY song_genres_name_unique (Name),
    UNIQUE KEY song_genres_encryptedId_unique (EncryptedId)
);

CREATE TABLE IF NOT EXISTS SongMoods(
    Id BIGINT AUTO_INCREMENT NOT NULL,
    EncryptedId varchar(256) NULL,
    Name varchar(32) NOT NULL,
    Color varchar(7),

    PRIMARY KEY (Id),
    UNIQUE KEY song_moods_name_unique (Name),
    UNIQUE KEY song_moods_encryptedId_unique (EncryptedId)
);

CREATE TABLE IF NOT EXISTS Songs(
    Id BIGINT AUTO_INCREMENT NOT NULL,
    EncryptedId varchar(256) NULL,
    Title varchar(128) NOT NULL,
    SoundLink varchar(512) not null,
    ThumbnailPath varchar(512) NULL,
    NumberOfListens INT NOT NULL DEFAULT 0,
    ReleaseDate TIMESTAMP NOT NULL,

    PRIMARY KEY (Id),
    UNIQUE KEY songs_title_unique (Title),
    UNIQUE KEY songs_soundLink_unique (SoundLink),
    UNIQUE KEY songs_thumbnailPath_unique (ThumbnailPath),
    UNIQUE KEY songs_encryptedId_unique (EncryptedId)
);

CREATE TABLE IF NOT EXISTS SongAuthors(
    Id BIGINT AUTO_INCREMENT NOT NULL,
    EncryptedId varchar(256) NULL,
    Name varchar(32) NOT NULL,

    PRIMARY KEY (Id),
    UNIQUE KEY song_authors_name_unique (Name),
    UNIQUE KEY song_authors_encryptedId_unique (EncryptedId)
);

CREATE TABLE IF NOT EXISTS SongAuthorsLinks(
    SongId BIGINT NOT NULL,
    AuthorId BIGINT NOT NULL,

    PRIMARY KEY (SongId, AuthorId),
    FOREIGN KEY (SongId) REFERENCES Songs(Id) ON DELETE CASCADE,
    FOREIGN KEY (AuthorId) REFERENCES SongAuthors(Id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS SongGenresLinks(
    SongId BIGINT NOT NULL,
    GenreId BIGINT NOT NULL,

    PRIMARY KEY (SongId, GenreId),
    FOREIGN KEY (SongId) REFERENCES Songs(Id) ON DELETE CASCADE,
    FOREIGN KEY (GenreId) REFERENCES SongGenres(Id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS SongMoodsLinks(
    SongId BIGINT NOT NULL,
    MoodId BIGINT NOT NULL,

    PRIMARY KEY (SongId, MoodId),
    FOREIGN KEY (SongId) REFERENCES Songs(Id) ON DELETE CASCADE,
    FOREIGN KEY (MoodId) REFERENCES SongMoods(Id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Playlists(
    Id BIGINT AUTO_INCREMENT NOT NULL,
    EncryptedId varchar(256) NULL,
    Name varchar(128) NOT NULL,
    CreatedDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UserId BIGINT NOT NULL,

    PRIMARY KEY (Id),
    UNIQUE KEY playlists_encryptedId_unique (EncryptedId),
    UNIQUE KEY playlists_name_and_userId_unique (Name, UserId)
);

CREATE TABLE IF NOT EXISTS PlaylistsSongsLinks(
    PlaylistId BIGINT NOT NULL,
    SongId BIGINT NOT NULL,

    PRIMARY KEY (SongId, PlaylistId),
    FOREIGN KEY (SongId) REFERENCES Songs(Id) ON DELETE CASCADE,
    FOREIGN KEY (PlaylistId) REFERENCES Playlists(Id) ON DELETE CASCADE
);
