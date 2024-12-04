package com.example.pequenoexploradorapp.di

import com.example.pequenoexploradorapp.network.ApiService
import com.example.pequenoexploradorapp.repository.RemoteRepositoryImpl
import io.ktor.client.HttpClient
import org.koin.dsl.module

val repositoryModule = module {
    single { provideRemoteRepository(get(), get()) }
}

fun provideRemoteRepository(apiService: ApiService, client: HttpClient): RemoteRepositoryImpl {
    return RemoteRepositoryImpl(apiService, client)
}
