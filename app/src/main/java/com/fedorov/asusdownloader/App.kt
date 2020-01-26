package com.fedorov.asusdownloader

import android.app.Application
import com.fedorov.asusdownloader.di.AppComponent
import com.fedorov.asusdownloader.di.DaggerAppComponent

class App : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}