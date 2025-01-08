package com.example.playlistmaker.domain.media.entity

import android.os.Parcelable
import com.example.playlistmaker.domain.search.entity.Track
import org.parceler.Parcel

@Parcel
data class Playlist(
    val playlistId: Long?,
    val playlistName: String?, // Название плейлиста
    val playlistInfo: String?, // Описание плейлиста
    val playlistImgPath: String?, // Путь к файлу изображения для обложки
    val trackIds: String?, // Список идентификаторов треков
    val tracksCount: Int, // Количество треков
) : Parcelable {
    constructor(parcel: android.os.Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
    )

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        var id = playlistId
        if (id == null) {
            id = 0
        }
        parcel.writeLong(id)
        parcel.writeString(playlistName)
        parcel.writeString(playlistInfo)
        parcel.writeString(playlistImgPath)
        parcel.writeString(trackIds)
        parcel.writeInt(tracksCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: android.os.Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }
}