package com.example.playlistmaker.domain.media.entity

data class Playlist(
    val playlistId: Long?,
    val playlistName: String?, // Название плейлиста
    val playlistInfo: String?, // Описание плейлиста
    val playlistImgPath: String?, // Путь к файлу изображения для обложки
    val trackIds: String?, // Список идентификаторов треков
    val tracksCount: Int, // Количество треков
)