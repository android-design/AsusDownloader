package com.fedorov.asusdownloader.storage

interface Storage {
    fun getString(key: String, defaultValue: String): String
    fun setString(key: String, value: String)
}