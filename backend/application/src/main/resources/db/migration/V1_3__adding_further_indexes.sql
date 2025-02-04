ALTER TABLE Songs ADD INDEX idx_songs_encryptedid (EncryptedId);
ALTER TABLE SongMoods ADD INDEX idx_song_moods_encryptedid (EncryptedId);
ALTER TABLE SongGenres ADD INDEX idx_song_genres_encryptedid (EncryptedId);