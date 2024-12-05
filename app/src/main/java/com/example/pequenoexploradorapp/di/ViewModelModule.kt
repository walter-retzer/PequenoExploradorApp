package com.example.pequenoexploradorapp.di

import android.content.Context
import com.example.pequenoexploradorapp.domain.connectivity.AndroidConnectivityObserver
import com.example.pequenoexploradorapp.domain.secure.SharedPrefApp
import com.example.pequenoexploradorapp.presentation.viewmodel.LoginUserViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.SignInViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


val viewModelModules = module {
    single { provideSharedPref(androidApplication()) }
    single { provideConnectivityStatus(androidApplication()) }
    factory<LoginUserViewModel> { LoginUserViewModel(provideConnectivityStatus(androidApplication()), get()) }
    factory<SignInViewModel> { SignInViewModel(get(), get()) }
}

fun provideConnectivityStatus(context: Context): AndroidConnectivityObserver {
    return AndroidConnectivityObserver(context)
}

fun provideSharedPref(context: Context): SharedPrefApp {
    return SharedPrefApp(context)
}
