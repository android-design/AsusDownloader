package com.fedorov.asusdownloader.di

import com.fedorov.asusdownloader.storage.SharedPreferencesStorage
import com.fedorov.asusdownloader.storage.Storage
import dagger.Binds
import dagger.Module

@Module
abstract class StorageModule {

    @Binds
    abstract fun provideStorage(storage: SharedPreferencesStorage): Storage
}