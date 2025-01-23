package com.example.pequenoexploradorapp.di

import com.example.pequenoexploradorapp.domain.database.NasaImageDao
import com.example.pequenoexploradorapp.domain.repository.local.FavouriteImageRepositoryImpl
import com.example.pequenoexploradorapp.domain.repository.remote.RemoteRepositoryImpl
import io.ktor.client.HttpClient
import org.koin.dsl.module

val repositoryModule = module {
    single { provideRemoteRepository(get()) }
    single { provideLocalFavouriteImageRepository(get()) }
}

fun provideRemoteRepository(client: HttpClient): RemoteRepositoryImpl {
    return RemoteRepositoryImpl(client)
}

fun provideLocalFavouriteImageRepository(dao: NasaImageDao): FavouriteImageRepositoryImpl {
    return FavouriteImageRepositoryImpl(dao)
}
