package com.example.pequenoexploradorapp.di

import androidx.room.Room
import com.example.pequenoexploradorapp.domain.database.NasaImagesDatabase
import com.example.pequenoexploradorapp.domain.repository.NasaImageRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val dataBaseRoomModules = module {
    singleOf(::NasaImageRepository)
    single {
        Room.databaseBuilder(
            androidContext(),
            NasaImagesDatabase::class.java, "nasa-images.db"
        ).build()
    }
    single {
        get<NasaImagesDatabase>().taskDao()
    }
}
