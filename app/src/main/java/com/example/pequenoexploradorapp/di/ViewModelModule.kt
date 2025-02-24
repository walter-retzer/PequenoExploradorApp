package com.example.pequenoexploradorapp.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.example.pequenoexploradorapp.domain.connectivity.AndroidConnectivityObserver
import com.example.pequenoexploradorapp.domain.secure.SharedPrefApp
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadFavouriteImageViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadNasaImageViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadNasaVideoViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadRoverImageViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.LoginUserViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.NasaVideoDetailViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.PictureOfTheDayViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.ProfileViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.QuestionsViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.RoverMissionDetailViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.SearchImageViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.SearchNasaViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.SignInViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


val viewModelModules = module {
    single { provideSharedPref(androidApplication()) }
    single { provideConnectivityStatus(androidApplication()) }
    factory<LoginUserViewModel> {
        LoginUserViewModel(
            provideConnectivityStatus(androidApplication()),
            get()
        )
    }
    factory<SignInViewModel> {
        SignInViewModel(
            provideConnectivityStatus(androidApplication()),
            get()
        )
    }
    factory<SearchImageViewModel> {
        SearchImageViewModel(
            provideConnectivityStatus(androidApplication())
        )
    }
    factory<LoadNasaImageViewModel> {
        LoadNasaImageViewModel(
            provideConnectivityStatus(androidApplication()),
            get(),
            get()
        )
    }
    factory<PictureOfTheDayViewModel> {
        PictureOfTheDayViewModel(
            provideConnectivityStatus(androidApplication()),
            get(),
            get()
        )
    }
    factory<RoverMissionDetailViewModel> {
        RoverMissionDetailViewModel(
            provideConnectivityStatus(androidApplication()),
            get()
        )
    }
    factory<LoadRoverImageViewModel> {
        LoadRoverImageViewModel(
            provideConnectivityStatus(androidApplication()),
            get(),
            get()
        )
    }
    factory<LoadFavouriteImageViewModel> {
        LoadFavouriteImageViewModel(
            provideConnectivityStatus(androidApplication()),
            get()
        )
    }
    factory<LoadNasaVideoViewModel> {
        LoadNasaVideoViewModel(
            provideConnectivityStatus(androidApplication()),
            get(),
            get()
        )
    }
    factory<NasaVideoDetailViewModel> {
        NasaVideoDetailViewModel(
            provideConnectivityStatus(androidApplication()),
            get(),
            SavedStateHandle()
        )
    }
    factory<SearchNasaViewModel> {
        SearchNasaViewModel(
            provideConnectivityStatus(androidApplication())
        )
    }
    factory<ProfileViewModel> {
        ProfileViewModel(get())
    }
    factory<QuestionsViewModel> {
        QuestionsViewModel(
            get(),
            provideConnectivityStatus(androidApplication())
        )
    }
}

fun provideConnectivityStatus(context: Context): AndroidConnectivityObserver {
    return AndroidConnectivityObserver(context)
}

fun provideSharedPref(context: Context): SharedPrefApp {
    return SharedPrefApp(context)
}
