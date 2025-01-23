package com.example.pequenoexploradorapp.domain.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pequenoexploradorapp.domain.datasource.typeConverter.Converters

@Database(entities = [FavouriteImageEntity::class], version = 2003)
@TypeConverters(Converters::class)
abstract class FavouriteImageDatabase : RoomDatabase() {
    abstract fun taskDao(): FavouriteImageDao
}
