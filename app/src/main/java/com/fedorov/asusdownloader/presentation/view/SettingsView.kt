package com.fedorov.asusdownloader.presentation.view

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface SettingsView : MvpView {
    fun initView(
        _serverAddress: String,
        _serverPort: String,
        _serverLogin: String,
        _serverPassword: String
    )
}