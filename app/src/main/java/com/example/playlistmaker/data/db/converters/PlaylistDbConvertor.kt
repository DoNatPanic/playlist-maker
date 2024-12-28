package com.example.playlistmaker.data.db.converters

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.domain.media.entity.Playlist

class PlaylistDbConvertor {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            0,
            playlist.playlistName,
            playlist.playlistInfo,
            playlist.playlistImgPath,
            playlist.trackIds,
            playlist.tracksCount
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        return Playlist(
            entity.playlistName,
            entity.playlistInfo,
            entity.playlistImgPath,
            entity.trackIds,
            entity.tracksCount
        )
    }
}