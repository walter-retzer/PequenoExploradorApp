package com.example.pequenoexploradorapp.di

import android.content.Context
import com.example.pequenoexploradorapp.domain.connectivity.AndroidConnectivityObserver
import com.example.pequenoexploradorapp.presentation.viewmodel.LoginUserViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.SignInViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val viewModelModules = module {
    single { provideConnectivityStatus(androidApplication())}
    factory <LoginUserViewModel> { LoginUserViewModel(provideConnectivityStatus(androidApplication())) }
    factory <SignInViewModel> {  SignInViewModel(get()) }
}

fun provideConnectivityStatus(client: Context): AndroidConnectivityObserver {
    return AndroidConnectivityObserver(client)
}
