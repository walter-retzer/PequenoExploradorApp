package com.example.pequenoexploradorapp.domain.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NasaImageDao {

    @Query("SELECT * FROM NasaImageEntity")
    suspend fun findAll(): List<NasaImageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(task: NasaImageEntity)

}