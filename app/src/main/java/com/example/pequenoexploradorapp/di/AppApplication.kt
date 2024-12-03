package com.example.pequenoexploradorapp.di

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        startKoin {
            androidLogger()
            androidContext(this@AppApplication)
            modules(
                appModule, provideHttpClientModule
            )
        }
    }

    companion object{
        var appContext: Context? = null
    }

}
