ALTER TABLE Playlists ADD CONSTRAINT fk_playlists_userId FOREIGN KEY (UserId) REFERENCES Users(Id);