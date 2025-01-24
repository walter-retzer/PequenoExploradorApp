package com.example.pequenoexploradorapp.domain.datasource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteImageDao {

    @Query("SELECT * FROM favourites ORDER BY id DESC")
    suspend fun findAll(): List<FavouriteImageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(favourite: FavouriteImageEntity)

    @Query("SELECT * FROM favourites WHERE id = :id")
    fun findById(id: String): Flow<FavouriteImageEntity?>

    @Delete
    suspend fun deleteImageFavourite(favourite: FavouriteImageEntity)

    @Query("DELETE FROM favourites")
    suspend fun deleteAll()

}