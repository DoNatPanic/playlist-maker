package com.example.playlistmaker.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id")
    val playlistId: Long,
    @ColumnInfo(name = "playlist_name")
    val playlistName: String?, // Название плейлиста
    @ColumnInfo(name = "playlist_info")
    val playlistInfo: String?, // Описание плейлиста
    @ColumnInfo(name = "playlist_img_path")
    val playlistImgPath: String?, // Путь к файлу изображения для обложки
    @ColumnInfo(name = "track_ids")
    val trackIds: String?, // Список идентификаторов треков
    @ColumnInfo(name = "tracks_count")
    val tracksCount: Int, // Количество треков
)