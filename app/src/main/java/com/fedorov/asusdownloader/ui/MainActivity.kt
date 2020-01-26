package com.fedorov.asusdownloader.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.fedorov.asusdownloader.App
import com.fedorov.asusdownloader.R
import com.fedorov.asusdownloader.presentation.presenter.MainPresenter
import com.fedorov.asusdownloader.presentation.view.MainView
import com.fedorov.asusdownloader.ui.base.BaseFragment
import com.fedorov.asusdownloader.ui.fragment.connectionSettings.ConnectionSettings
import com.fedorov.asusdownloader.ui.fragment.dashboard.Dashboard
import kotlinx.android.synthetic.main.toolbar.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject


class MainActivity : MvpAppCompatActivity(), MainView {

    @Inject
    @InjectPresenter
    internal lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    private var currentOptionsMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        currentOptionsMenu = menu
        presenter.createMenu(currentFragmentInContainer())

        return true
    }

    override fun doInflateMenu(){
        currentOptionsMenu?.let {
            menuInflater.inflate(R.menu.menu_main, it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_torrent -> {
                presenter.addTorrent(currentFragmentInContainer())
            }
            R.id.add_link_torrent -> {
                // in future add this feature.
            }
            R.id.start_all_items -> {
                presenter.startAllItems(currentFragmentInContainer())
            }
            R.id.pause_all_items -> {
                presenter.pauseAllItems(currentFragmentInContainer())
            }
            R.id.clear_all_completed_items -> {
                presenter.clearAllCompletedItems(currentFragmentInContainer())
            }
            R.id.settings -> {
                presenter.openSettings()
            }
        }
        return true
    }

    override fun onContextMenuClosed(menu: Menu) {
        presenter.contextMenuClosed(currentFragmentInContainer())
    }

    override fun onBackPressed() {
        presenter.onBackPressed(currentFragmentInContainer())
    }

    override fun doOnBackPressed() {
        super.onBackPressed()
    }

    override fun doInvalidateOptionsMenu(){
        invalidateOptionsMenu()
    }

    override fun openSettings(){
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = ConnectionSettings()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun addDashboardFragmentInContainer(){
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = Dashboard()
        transaction.add(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    private fun currentFragmentInContainer() =
        supportFragmentManager.findFragmentById(R.id.fragmentContainer) as BaseFragment
}