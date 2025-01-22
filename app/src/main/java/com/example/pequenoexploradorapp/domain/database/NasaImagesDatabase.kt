package com.example.pequenoexploradorapp.domain.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NasaImageEntity::class], version = 1)
abstract class NasaImagesDatabase : RoomDatabase() {

    abstract fun taskDao(): NasaImageDao

}