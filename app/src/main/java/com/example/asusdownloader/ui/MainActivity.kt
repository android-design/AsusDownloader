package com.example.asusdownloader.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.asusdownloader.Const.Companion.FILE_TYPE
import com.example.asusdownloader.Const.Companion.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
import com.example.asusdownloader.Const.Companion.REQUEST_CODE_SEND_FILE
import com.example.asusdownloader.ItemsOperation
import com.example.asusdownloader.R
import com.example.asusdownloader.data.model.Torrent
import com.example.asusdownloader.presentation.presenter.MainPresenter
import com.example.asusdownloader.presentation.view.MainView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter


class MainActivity : MvpAppCompatActivity(), MainView {

    companion object {
        fun chooseTorrentFileIntent(): Intent {
            val chooseFile = Intent(Intent.ACTION_GET_CONTENT)
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
            chooseFile.type = FILE_TYPE
            return chooseFile
        }
    }


    @InjectPresenter
    internal lateinit var presenter: MainPresenter

    private val adapter =
        RvAdapter(onContextMenuShow = { currentTorrent ->
            presenter.itemContextMenuOpened(
                currentTorrent
            )
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)

        swiperefresh.setOnRefreshListener {
            presenter.startManualRefresh()
        }

        rv.layoutManager = LinearLayoutManager(this)
        rv.setHasFixedSize(true)

        rv.adapter = adapter
        registerForContextMenu(rv)
    }

    override fun onResume() {
        super.onResume()
        presenter.startUpdateItems()
    }

    override fun onPause() {
        super.onPause()
        presenter.stopUpdateItems()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.logout()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    presenter.startChooseFile()
                } else {
                    presenter.showMsg(getString(R.string.text_request_permission_denieded))
                }
            }
        }
    }

    override fun stopRefreshAnimation() {
        swiperefresh.isRefreshing = false
    }

    override fun chooseTorrentFileToAdd() {
        startActivityForResult(chooseTorrentFileIntent(), REQUEST_CODE_SEND_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SEND_FILE && resultCode == Activity.RESULT_OK && data != null) {
            data.data?.let { uri ->
                contentResolver.openInputStream(uri)
                    ?.let { stream -> presenter.sendFile(stream, cacheDir) }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_torrent -> {
                pickFile()
            }
        }
        return true
    }

    private fun pickFile() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )
            }
        } else {
            presenter.startChooseFile()
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.startMenuItem -> {
                presenter.operationWithItem(ItemsOperation.START)
                true
            }
            R.id.pauseMenuItem -> {
                presenter.operationWithItem(ItemsOperation.PAUSE)
                true
            }
            R.id.removeMenuItem -> {
                presenter.operationWithItem(ItemsOperation.REMOVE)
                true
            }
            else -> {
                super.onContextItemSelected(item)
            }
        }

    override fun onContextMenuClosed(menu: Menu) {
        presenter.startUpdateItems()
    }

    override fun updateListTorrents(items: List<Torrent>) {
        adapter.items = items
    }

    override fun showMsg(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    override fun showMsgFromResouce(id: Int) {
        showMsg(getString(id))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }
}