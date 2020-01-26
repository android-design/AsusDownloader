package com.fedorov.asusdownloader.di

import com.fedorov.asusdownloader.data.provider.AsusServerProvider
import com.fedorov.asusdownloader.data.provider.RemoteDataProvider
import dagger.Binds
import dagger.Module

@Module
abstract class DataProviderModule {
    @Binds
    abstract fun provideRemoteData(asusServer: AsusServerProvider): RemoteDataProvider
}