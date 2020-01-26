package com.fedorov.asusdownloader.presentation.presenter

import com.fedorov.asusdownloader.settings.Preferences
import com.fedorov.asusdownloader.presentation.view.SettingsView
import moxy.InjectViewState
import moxy.MvpPresenter

import javax.inject.Inject

@InjectViewState
class SettingsPresenter @Inject constructor(val preferences: Preferences) :
    MvpPresenter<SettingsView>() {

    private var isNeedUpdatePreferences = false

    override fun onDestroy() {
        super.onDestroy()
        if (isNeedUpdatePreferences) {
            preferences.savePreferences()
        }
    }

    fun serverAddressUpdate(newValue: String) {
        if (!preferences.address.equals(newValue)) {
            preferences.address = newValue
            isNeedUpdatePreferences = true
        }
    }

    fun serverPortUpdate(newValue: String) {
        if (!preferences.port.equals(newValue)) {
            preferences.port = newValue
            isNeedUpdatePreferences = true
        }
    }

    fun serverLoginUpdate(newValue: String) {
        if (!preferences.login.equals(newValue)) {
            preferences.login = newValue
            isNeedUpdatePreferences = true
        }
    }

    fun serverPasswordUpdate(newValue: String) {
        if (!preferences.password.equals(newValue)) {
            preferences.password = newValue
            isNeedUpdatePreferences = true
        }
    }

    fun initView() {
        viewState.initView(
            preferences.address,
            preferences.port,
            preferences.login,
            preferences.password
        )
    }

}