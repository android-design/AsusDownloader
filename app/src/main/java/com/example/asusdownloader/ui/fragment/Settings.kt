package com.example.asusdownloader.ui.fragment

import android.widget.Toast
import com.example.asusdownloader.R
import com.example.asusdownloader.presentation.presenter.SettingsPresenter
import com.example.asusdownloader.presentation.view.SettingsView
import com.example.asusdownloader.ui.base.BaseFragment
import moxy.presenter.InjectPresenter

class Settings : BaseFragment(), SettingsView {
    override val layoutRes = R.layout.fragment_settings

    @InjectPresenter
    internal lateinit var presenter: SettingsPresenter

}