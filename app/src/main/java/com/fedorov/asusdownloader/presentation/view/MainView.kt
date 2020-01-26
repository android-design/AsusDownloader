package com.fedorov.asusdownloader.presentation.view

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface MainView : MvpView {
    fun doInvalidateOptionsMenu()
    fun doOnBackPressed()
    fun doInflateMenu()
    fun addDashboardFragmentInContainer()
    fun openSettings()
}