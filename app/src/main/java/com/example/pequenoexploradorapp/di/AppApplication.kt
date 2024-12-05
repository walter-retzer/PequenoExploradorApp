package com.example.pequenoexploradorapp.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val appModule = module {
            includes(viewModelModules, networkModule, repositoryModule)
        }

        startKoin {
            androidLogger()
            androidContext(this@AppApplication)
            modules( appModule)
        }
    }

}
