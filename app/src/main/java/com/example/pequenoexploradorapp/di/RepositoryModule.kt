package com.example.pequenoexploradorapp.di

import com.example.pequenoexploradorapp.domain.repository.RemoteRepositoryImpl
import io.ktor.client.HttpClient
import org.koin.dsl.module

val repositoryModule = module {
    single { provideRemoteRepository(get()) }
}

fun provideRemoteRepository(client: HttpClient): RemoteRepositoryImpl {
    return RemoteRepositoryImpl(client)
}
