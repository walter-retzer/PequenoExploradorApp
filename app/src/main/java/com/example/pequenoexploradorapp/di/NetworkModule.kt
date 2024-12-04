package com.example.pequenoexploradorapp.di

import com.example.pequenoexploradorapp.domain.network.httpClientAndroid
import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    single { provideHttpClient() }
}

fun provideHttpClient(): HttpClient {
    return httpClientAndroid
}
