package com.example.pequenoexploradorapp.di

import com.example.pequenoexploradorapp.network.ApiService
import com.example.pequenoexploradorapp.repository.RemoteRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single { provideRemoteRepository(get()) }
}

fun provideRemoteRepository(apiService: ApiService): RemoteRepositoryImpl {
    return RemoteRepositoryImpl(apiService)
}
