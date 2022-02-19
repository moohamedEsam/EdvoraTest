package com.example.edvoratest.utils

import android.app.Application
import com.example.edvoratest.koin.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ApplicationClass: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ApplicationClass)
           modules(appModule)
        }
    }
}