package com.example.pequenoexploradorapp.domain.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pequenoexploradorapp.domain.database.typeConverter.Converters

@Database(entities = [NasaImageEntity::class], version = 2001)
@TypeConverters(Converters::class)
abstract class NasaImagesDatabase : RoomDatabase() {
    abstract fun taskDao(): NasaImageDao
}
