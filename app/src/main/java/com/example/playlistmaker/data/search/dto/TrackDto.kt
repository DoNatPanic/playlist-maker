package com.example.playlistmaker.data.search.dto

import com.google.gson.annotations.SerializedName

data class TrackDto(
    val trackId: Long,
    val trackName: String?, // Название композиции
    val artistName: String?, // Имя исполнителя
    @SerializedName("trackTimeMillis")
    val trackTime: Int, // Продолжительность трека
    val artworkUrl100: String?, // Ссылка на изображение обложки
    val collectionName: String?, // Название альбома
    val releaseDate: String?, // Год релиза трека
    val primaryGenreName: String?, // Жанр трека
    val country: String?,  // Страна исполнителя
    val previewUrl: String?, // Ссылка на отрывок трека
    var isFavorite: Boolean
)