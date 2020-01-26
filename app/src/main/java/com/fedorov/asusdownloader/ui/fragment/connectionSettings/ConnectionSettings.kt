package com.fedorov.asusdownloader.ui.fragment.connectionSettings

import android.content.Context
import android.os.Bundle
import android.view.View
import com.fedorov.asusdownloader.App
import com.fedorov.asusdownloader.R
import com.fedorov.asusdownloader.presentation.presenter.SettingsPresenter
import com.fedorov.asusdownloader.presentation.view.SettingsView
import com.fedorov.asusdownloader.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_settings.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class ConnectionSettings : BaseFragment(), SettingsView {
    override val layoutRes = R.layout.fragment_settings

    @Inject
    @InjectPresenter
    internal lateinit var presenter: SettingsPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.initView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity!!.application as App).appComponent.inject(this)
    }

    override fun initView(
        _serverAddress: String,
        _serverPort: String,
        _serverLogin: String,
        _serverPassword: String
    ) {
        serverAddress.setText(_serverAddress)
        serverPort.setText(_serverPort)
        login.setText(_serverLogin)
        password.setText(_serverPassword)

        serverAddress.setOnFocusChangeListener { _, b ->
            if (!b){
                presenter.serverAddressUpdate(serverAddress.text.toString())
            }
        }

        serverPort.setOnFocusChangeListener { _, b ->
            if (!b){
                presenter.serverPortUpdate(serverPort.text.toString())
            }
        }

        login.setOnFocusChangeListener { _, b ->

            if (!b){
                presenter.serverLoginUpdate(login.text.toString())
            }
        }

        password.setOnFocusChangeListener { _, b ->
            if (!b){
                presenter.serverPasswordUpdate(password.text.toString())
            }
        }
    }

    override fun allowBackPressed(): Boolean {
        if (isServerAddressEmpty()) return false
        if (isServerPortEmpty()) return false
        if (isLoginEmpty()) return false
        if (isPasswordEmpty()) return false

        return true
    }

    private fun isPasswordEmpty(): Boolean {
        if (password.text.isEmpty()) {
            password.requestFocus()
            password.error = getString(R.string.error_need_password)

            return true
        }
        return false
    }

    private fun isLoginEmpty(): Boolean {
        if (login.text.isEmpty()) {
            login.requestFocus()
            login.error = getString(R.string.error_need_login)

            return true
        }
        return false
    }

    private fun isServerPortEmpty(): Boolean {
        if (serverPort.text.isEmpty()) {
            serverPort.requestFocus()
            serverPort.error = getString(R.string.error_need_server_port)

            return true
        }
        return false
    }

    private fun isServerAddressEmpty(): Boolean {
        if (serverAddress.text.isEmpty()) {
            serverAddress.requestFocus()
            serverAddress.error = getString(R.string.error_need_address)

            return true
        }
        return false
    }
}