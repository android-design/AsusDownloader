package com.fedorov.asusdownloader.presentation.presenter

import com.fedorov.asusdownloader.presentation.view.MainView
import com.fedorov.asusdownloader.settings.Preferences
import com.fedorov.asusdownloader.ui.base.BaseFragment
import com.fedorov.asusdownloader.ui.fragment.dashboard.Dashboard
import com.fedorov.asusdownloader.ui.fragment.dashboard.DashboardCommunicator
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject


@InjectViewState
class MainPresenter @Inject constructor(
    val preferences: Preferences
) : MvpPresenter<MainView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        initFragmentsOnViewAttach()
    }

    private fun initFragmentsOnViewAttach() {
        viewState.addDashboardFragmentInContainer()

        if (isNeedOpenSettingsOnStartup()) {
            viewState.openSettings()
        }
    }

    private fun isNeedOpenSettingsOnStartup() =
        preferences.address.isEmpty() || preferences.port.isEmpty() || preferences.login.isEmpty() || preferences.password.isEmpty()

    private fun invalidateOptionsMenu() {
        viewState.doInvalidateOptionsMenu()
    }

    fun onBackPressed(fr: BaseFragment) {
        fr.let {
            if (
                it.allowBackPressed()
            ) {
                viewState.doOnBackPressed()
            }
        }
        invalidateOptionsMenu()
    }

    fun contextMenuClosed(fr: BaseFragment) {
        fr.let { (it as DashboardCommunicator).onContextMenuClosed() }
    }

    fun addTorrent(fr: BaseFragment) {
        fr.let { (it as DashboardCommunicator).pickFile() }
    }

    fun pauseAllItems(fr: BaseFragment) {
        fr.let { (it as DashboardCommunicator).pauseAllItems() }
    }

    fun clearAllCompletedItems(fr: BaseFragment) {
        fr.let { (it as DashboardCommunicator).clearAllCompletedItems() }
    }

    fun createMenu(fr: BaseFragment) {

        if (fr is Dashboard)
            viewState.doInflateMenu()
    }

    fun startAllItems(fr: BaseFragment) {
        fr.let { (it as DashboardCommunicator).startAllItems() }
    }

    fun openSettings() {
        viewState.openSettings()
        viewState.doInvalidateOptionsMenu()
    }
}