package com.fedorov.asusdownloader.storage

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesStorage @Inject constructor(context: Context) : Storage {

    private val sharedPreferences =
        context.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)

    override fun setString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    override fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue)!!
    }
}