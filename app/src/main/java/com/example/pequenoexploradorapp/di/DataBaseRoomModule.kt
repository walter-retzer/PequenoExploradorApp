package com.example.pequenoexploradorapp.di

import androidx.room.Room
import com.example.pequenoexploradorapp.domain.datasource.local.FavouriteImageDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val dataBaseRoomModules = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            FavouriteImageDatabase::class.java,
            "nasa-images.db"
        ).build()
    }
    single {
        get<FavouriteImageDatabase>().taskDao()
    }
}
