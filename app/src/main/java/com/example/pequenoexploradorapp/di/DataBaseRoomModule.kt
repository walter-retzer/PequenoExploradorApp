package com.example.pequenoexploradorapp.di

import androidx.room.Room
import com.example.pequenoexploradorapp.domain.database.NasaImagesDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val dataBaseRoomModules = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            NasaImagesDatabase::class.java,
            "nasa-images.db"
        ).build()
    }
    single {
        get<NasaImagesDatabase>().taskDao()
    }
}
