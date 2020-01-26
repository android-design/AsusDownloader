package com.fedorov.asusdownloader.di

import android.content.Context
import com.fedorov.asusdownloader.ui.MainActivity
import com.fedorov.asusdownloader.ui.fragment.dashboard.Dashboard
import com.fedorov.asusdownloader.ui.fragment.connectionSettings.ConnectionSettings
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataProviderModule::class, NetModule::class, StorageModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: Dashboard)
    fun inject(activity: ConnectionSettings)
    fun inject(activity: MainActivity)
}