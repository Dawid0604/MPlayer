ALTER TABLE Songs ADD FULLTEXT INDEX idx_songs_title (Title);
ALTER TABLE SongAuthors ADD FULLTEXT INDEX idx_song_authors_name (Name);