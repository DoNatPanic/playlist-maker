package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.FavouriteEntity

@Dao
interface FavouriteDao {

    @Insert(entity = FavouriteEntity::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertFavourite(track: FavouriteEntity)

    @Delete(entity = FavouriteEntity::class)
    fun deleteFavourite(entity: FavouriteEntity)

    @Query("SELECT * FROM favourites_table")
    fun getFavourites(): List<FavouriteEntity>

    @Query("SELECT * FROM favourites_table WHERE track_id = :id")
    fun getFavouriteById(id: Long): FavouriteEntity

    @Query("SELECT track_id FROM favourites_table")
    fun getFavouritesIds(): List<Long>
}