package com.example.pequenoexploradorapp.di

import com.example.pequenoexploradorapp.viewmodel.LoginUserViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    //viewModelOf(::LoginUserViewModel)
    factory <LoginUserViewModel> { LoginUserViewModel() }
}

