package com.example.asusdownloader.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.asusdownloader.R
import com.example.asusdownloader.presentation.presenter.MainPresenter
import com.example.asusdownloader.presentation.view.MainView
import com.example.asusdownloader.ui.fragment.Dashboard
import com.example.asusdownloader.ui.fragment.Settings
import kotlinx.android.synthetic.main.toolbar.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter


class MainActivity : MvpAppCompatActivity(), MainView {

    @InjectPresenter
    internal lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val transaction = supportFragmentManager.beginTransaction()
        val fragment = Dashboard()
        transaction.add(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_torrent -> {
                childFragmentDashboard().let { (it as DashboardCommunicator).pickFile() }

            }
            R.id.settings -> {
                val transaction = supportFragmentManager.beginTransaction()
                val fragment = Settings()
                transaction.replace(R.id.fragmentContainer, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
        return true
    }

    override fun onContextMenuClosed(menu: Menu) {
        childFragmentDashboard().let { (it as DashboardCommunicator).onContextMenuClosed() }
    }

    private fun childFragmentDashboard() = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
}