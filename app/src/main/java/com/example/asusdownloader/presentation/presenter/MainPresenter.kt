package com.example.asusdownloader.presentation.presenter

import com.example.asusdownloader.Const.Companion.ACK_SUCESS_RESPONSE
import com.example.asusdownloader.Const.Companion.BT_EXIST_RESPONSE
import com.example.asusdownloader.ItemsOperation
import com.example.asusdownloader.Const.Companion.FILE_TYPE
import com.example.asusdownloader.R
import com.example.asusdownloader.data.Repository
import com.example.asusdownloader.data.model.States
import com.example.asusdownloader.data.model.Torrent
import com.example.asusdownloader.presentation.fileFromStream
import com.example.asusdownloader.presentation.view.MainView
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.InjectViewState
import moxy.MvpPresenter
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.InputStream


@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var disposableGetItemsByInterval: Disposable? = null
    private var currentItem: Torrent? = null


    private fun auth(
        onCompleteAction: (() -> Unit)? = null,
        onCompleteActionForever: (() -> Unit)? = null
    ) {
        compositeDisposable.add(
            Repository.auth()
                .doOnSuccess { onCompleteActionForever?.invoke() }
                .subscribe({ response ->
                    response.headers().get("Set-Cookie")?.let {
                        showMsgFromResource(R.string.auth_complete)
                        onCompleteAction?.invoke()
                    }
                        ?: showMsgFromResource(R.string.auth_error)
                }, { throwable ->
                    throwable.message?.let { errorMessage -> showMsg(errorMessage) }
                })
        )
    }

    fun logout() {
        compositeDisposable.add(
            Repository.logout()
                .subscribe()
        )
    }

    fun startUpdateItems() {
        disposableGetItemsByInterval?.let {
            if (it.isDisposed) {
                getItemsByInterval()
            }
        } ?: getItemsByInterval()
    }

    private fun getItemsByInterval() {
        disposableGetItemsByInterval =
            Repository.getItemsByInterval()
                .subscribe({ torrents ->
                    viewState.updateListTorrents(torrents)
                },
                    { e ->
                        if (isNeedAuthorization(e)) {
                            auth(onCompleteAction = {
                                stopUpdateItems()
                                startUpdateItems()
                            }
                            )
                        } else {
                            e.message?.let { errorMessage -> showMsg(errorMessage) }
                        }
                    })
    }

    private fun getItems(
        onCompleteAction: (() -> Unit)? = null,
        onErrorAction: (() -> Unit)? = null
    ) {
        compositeDisposable.add(
            Repository.getItems()
                .doOnComplete { onCompleteAction?.invoke() }
                .subscribe({ torrents ->
                    viewState.updateListTorrents(torrents)

                },
                    { e ->
                        if (isNeedAuthorization(e)) {
                            auth(onCompleteAction = { startUpdateItems() }
                                , onCompleteActionForever = { onCompleteAction?.invoke() })
                        } else {
                            e.message?.let { errorMessage -> showMsg(errorMessage) }
                        }
                        onErrorAction?.invoke()
                    })
        )
    }

    private fun isNeedAuthorization(e: Throwable?) = e is HttpException && e.code() == 598

    fun itemContextMenuOpened(torrent: Torrent) {
        currentItem = torrent
        stopUpdateItems()
    }

    fun stopUpdateItems() {
        disposableGetItemsByInterval?.dispose()
    }

    fun startChooseFile() {
        viewState.chooseTorrentFileToAdd()
    }

    fun startManualRefresh() {
        getItems(onCompleteAction = { stopManualRefresh() },
            onErrorAction = { stopManualRefresh() })
    }

    private fun stopManualRefresh() {
        viewState.stopRefreshAnimation()
    }

    fun operationWithItem(operation: ItemsOperation) {
        currentItem?.let { torrent ->
            when (operation) {
                ItemsOperation.START -> startItem(torrent)
                ItemsOperation.PAUSE -> pauseItem(torrent)
                ItemsOperation.REMOVE -> removeItem(torrent)
            }
        }
        currentItem = null
    }

    private fun startItem(torrent: Torrent) {
        makeRequest(
            request = Repository.startItem(torrent.id, torrent.type),
            doOnSuccess = { getItems() },
            onSuccess = { s -> showMsg(States.valueOf(s).title) })
    }

    private fun pauseItem(torrent: Torrent) {
        makeRequest(
            request = Repository.pauseItem(torrent.id, torrent.type),
            doOnSuccess = { getItems() },
            onSuccess = { s -> showMsg(States.valueOf(s).title) })
    }

    private fun makeRequest(
        request: Single<String>,
        doOnSuccess: (() -> Unit)? = null,
        onSuccess: ((s: String) -> Unit)? = null,
        onError: ((e: Throwable) -> Unit)? = null
    ) {
        compositeDisposable.add(request
            .doOnSuccess { doOnSuccess?.invoke() }
            .subscribe(
                { s -> onSuccess?.invoke(s) },
                { e -> onError?.invoke(e) }
            )
        )
    }

    private fun removeItem(torrent: Torrent) {
        makeRequest(
            request = Repository.removeItem(torrent.id, torrent.type),
            doOnSuccess = { getItems() },
            onSuccess = { s -> showMsg(States.valueOf(s).title) })
    }

    fun sendFile(stream: InputStream, dir: File) {
        val fileToSend =
            fileFromStream(stream, dir)
        val requestFile =
            RequestBody.create(MediaType.parse(FILE_TYPE), fileToSend)

        val body = MultipartBody.Part.createFormData("file", fileToSend.name, requestFile)

        compositeDisposable.add(Repository.sendTorrentFile(body)
            .doOnError { e ->
                showMsg(e.message.toString())
            }
            .flatMap {
                Repository.confirmDownload(fileToSend.name)
            }
            .subscribe({ s ->
                when {
                    s.contains(ACK_SUCESS_RESPONSE) -> showMsgFromResource(R.string.success_action)
                    s.contains(BT_EXIST_RESPONSE) -> showMsgFromResource(R.string.torrent_already_exist)
                    else -> showMsg(s)
                }
            }) { s ->
                showMsg(s.message.toString())
            })
    }

    fun showMsg(s: String) {
        viewState.showMsg(s)
    }

    private fun showMsgFromResource(id: Int) {
        viewState.showMsgFromResouce(id)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableGetItemsByInterval?.dispose()
        compositeDisposable.clear()
    }
}