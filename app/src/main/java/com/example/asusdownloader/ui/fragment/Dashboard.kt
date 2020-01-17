package com.example.asusdownloader.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.asusdownloader.Const
import com.example.asusdownloader.ItemsOperation
import com.example.asusdownloader.R
import com.example.asusdownloader.data.model.Torrent
import com.example.asusdownloader.presentation.presenter.DashBoardPresenter
import com.example.asusdownloader.presentation.view.DashBoardView
import com.example.asusdownloader.ui.DashboardCommunicator
import com.example.asusdownloader.ui.RvAdapter
import com.example.asusdownloader.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_dashboard.*
import moxy.presenter.InjectPresenter

class Dashboard() : BaseFragment(), DashBoardView, DashboardCommunicator {

    companion object {
        fun chooseTorrentFileIntent(): Intent {
            val chooseFile = Intent(Intent.ACTION_GET_CONTENT)
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
            chooseFile.type = Const.FILE_TYPE
            return chooseFile
        }
    }

    override val layoutRes = R.layout.fragment_dashboard

    @InjectPresenter
    internal lateinit var presenter: DashBoardPresenter

    private val adapter =
        RvAdapter(onContextMenuShow = { currentTorrent ->
            presenter.itemContextMenuOpened(
                currentTorrent
            )
        })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        swiperefresh.setOnRefreshListener {
            presenter.startManualRefresh()
        }

        rv.layoutManager = LinearLayoutManager(context)
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
            Const.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    presenter.startChooseFile()
                } else {
                    presenter.showMsg(getString(R.string.text_request_permission_denied))
                }
            }
        }
    }

    override fun stopRefreshAnimation() {
        swiperefresh.isRefreshing = false
    }

    override fun chooseTorrentFileToAdd() {
        startActivityForResult(chooseTorrentFileIntent(), Const.REQUEST_CODE_SEND_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Const.REQUEST_CODE_SEND_FILE && resultCode == Activity.RESULT_OK && data != null) {
            activity?.let {
                data.data?.let { uri ->
                    context?.contentResolver?.openInputStream(uri)
                        ?.let { stream -> presenter.sendFile(stream, it.cacheDir) }
                }
            }
        }
    }

    override fun pickFile() {
        context?.let {
            if (ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activity!!,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        Const.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                    )
                }
            } else {
                presenter.startChooseFile()
            }
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

    override fun onContextMenuClosed() {
        presenter.startUpdateItems()
    }

    override fun updateListTorrents(items: List<Torrent>) {
        adapter.items = items
    }

    override fun showMsg(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }

    override fun showMsgFromResource(id: Int) {
        showMsg(getString(id))
    }
}