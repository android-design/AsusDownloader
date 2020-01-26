package com.fedorov.asusdownloader.di

import com.fedorov.asusdownloader.settings.Preferences
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.CookieJar
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy

@Module
class NetModule {

    @Provides
    fun getRetrofit(preferences: Preferences): Retrofit {
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        val cookieJar: CookieJar = JavaNetCookieJar(cookieManager)
        val builder = OkHttpClient.Builder()

        builder.cookieJar(cookieJar)
        val client = builder.build()

        return Retrofit.Builder()
            .baseUrl("http://${preferences.address}:${preferences.port}/")
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}