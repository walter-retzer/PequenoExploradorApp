package com.example.pequenoexploradorapp.di

import com.example.pequenoexploradorapp.presentation.viewmodel.LoginUserViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.SignInViewModel
import org.koin.dsl.module

val viewModelModules = module {
    factory <LoginUserViewModel> { LoginUserViewModel() }
    factory <SignInViewModel> {  SignInViewModel(get()) }
}
