package com.example.pequenoexploradorapp.domain.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavouriteImageDao {

    @Query("SELECT * FROM FavouriteImageEntity")
    suspend fun findAll(): List<FavouriteImageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(task: FavouriteImageEntity)

}