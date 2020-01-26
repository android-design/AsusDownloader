package com.fedorov.asusdownloader.settings

import com.fedorov.asusdownloader.storage.Storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(private val storage: Storage) {

    companion object {
        private const val LOGIN_PREFERENCE = "LOGIN"
        private const val PASSWORD_PREFERENCE = "PASSWORD"
        private const val ADDRESS_PREFERENCE = "ADDRESS"
        private const val PORT_PREFERENCE = "PORT"

        const val DEFAULT_DELAY = 5L
        private const val DEFAULT_ADDRESS = "192.168.1.1"
        private const val DEFAULT_PORT = "8081"
        private const val DEFAULT_LOGIN = "admin"
    }

    lateinit var login: String
    lateinit var password: String
    lateinit var address: String
    lateinit var port: String
    val delay =
        DEFAULT_DELAY

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        login = storage.getString(
            LOGIN_PREFERENCE,
            DEFAULT_LOGIN
        )// ?: DEFAULT_LOGIN
        password = storage.getString(PASSWORD_PREFERENCE, "")// ?: ""
        address = storage.getString(
            ADDRESS_PREFERENCE,
            DEFAULT_ADDRESS
        )// ?: DEFAULT_ADDRESS
        port = storage.getString(
            PORT_PREFERENCE,
            DEFAULT_PORT
        )// ?: DEFAULT_PORT
    }

    fun savePreferences() {
        storage.setString(LOGIN_PREFERENCE, login)
        storage.setString(PASSWORD_PREFERENCE, password)
        storage.setString(ADDRESS_PREFERENCE, address)
        storage.setString(PORT_PREFERENCE, port)
    }
}