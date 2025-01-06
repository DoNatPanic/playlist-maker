package com.example.playlistmaker.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks_table")
data class TrackEntity(
    @PrimaryKey
    @ColumnInfo(name = "track_id")
    val trackId: Long,
    @ColumnInfo(name = "track_name")
    val trackName: String?, // Название композиции
    @ColumnInfo(name = "artist_name")
    val artistName: String?, // Имя исполнителя
    @ColumnInfo(name = "track_time")
    val trackTime: Int = 0, // Продолжительность трека
    @ColumnInfo(name = "artwork_url100")
    val artworkUrl100: String?, // Ссылка на изображение обложки
    @ColumnInfo(name = "collection_name")
    val collectionName: String?, // Название альбома
    @ColumnInfo(name = "release_date")
    val releaseDate: String?, // Год релиза трека
    @ColumnInfo(name = "primary_genre_name")
    val primaryGenreName: String?, // Жанр трека
    @ColumnInfo(name = "country")
    val country: String?,  // Страна исполнителя
    @ColumnInfo(name = "preview_url")
    val previewUrl: String?, // Ссылка на отрывок трека
)