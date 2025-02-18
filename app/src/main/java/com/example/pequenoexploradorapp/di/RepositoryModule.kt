package com.example.pequenoexploradorapp.di

import com.example.pequenoexploradorapp.domain.datasource.local.FavouriteImageDao
import com.example.pequenoexploradorapp.domain.repository.local.FavouriteImageRepositoryImpl
import com.example.pequenoexploradorapp.domain.repository.remote.FirebaseDataBaseRepositoryImpl
import com.example.pequenoexploradorapp.domain.repository.remote.RemoteRepositoryImpl
import io.ktor.client.HttpClient
import org.koin.dsl.module

val repositoryModule = module {
    single { provideRemoteRepository(get()) }
    single { provideLocalFavouriteImageRepository(get()) }
    single { FirebaseDataBaseRepositoryImpl() }
}

fun provideRemoteRepository(client: HttpClient): RemoteRepositoryImpl {
    return RemoteRepositoryImpl(client)
}

fun provideLocalFavouriteImageRepository(dao: FavouriteImageDao): FavouriteImageRepositoryImpl {
    return FavouriteImageRepositoryImpl(dao)
}
