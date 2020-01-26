package com.fedorov.asusdownloader.presentation.presenter

import com.fedorov.asusdownloader.settings.Const
import com.fedorov.asusdownloader.ItemsOperation
import com.fedorov.asusdownloader.settings.Preferences
import com.fedorov.asusdownloader.R
import com.fedorov.asusdownloader.data.Repository
import com.fedorov.asusdownloader.data.model.States
import com.fedorov.asusdownloader.data.model.Torrent
import com.fedorov.asusdownloader.presentation.fileFromStream
import com.fedorov.asusdownloader.presentation.view.DashBoardView
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import moxy.InjectViewState
import moxy.MvpPresenter
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@InjectViewState
class DashBoardPresenter @Inject constructor(private val repository: Repository) :
    MvpPresenter<DashBoardView>() {
    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }
    private var disposableGetItemsByInterval: Disposable? = null
    private var currentItem: Torrent? = null
    private val flowOfManualUpdates by lazy { PublishSubject.create<Boolean>() }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        compositeDisposable.add(flowOfManualUpdates.debounce(
            Preferences.DEFAULT_DELAY,
            TimeUnit.SECONDS
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    getItems(onCompleteAction = { stopManualRefresh() },
                        onErrorAction = { stopManualRefresh() })
                }, {
                    showMsg(it.message.toString())
                })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        logout()
        disposableGetItemsByInterval?.dispose()
        compositeDisposable.clear()
    }

    private fun logout() {
        compositeDisposable.add(repository.logout()
            .subscribe({}, {})
        )
    }

    private fun auth(
        onCompleteAction: (() -> Unit)? = null,
        onCompleteActionForever: (() -> Unit)? = null
    ) {
        compositeDisposable.add(
            repository.auth()
                .doOnSuccess { onCompleteActionForever?.invoke() }
                .subscribe({ response ->
                    response.headers().get("Set-Cookie")?.let {
                        showMsgFromResource(R.string.auth_complete)
                        onCompleteAction?.invoke()
                    }
                        ?: if (response.code() == 401) {
                            showMsgFromResource(R.string.auth_error_invalid_login_or_password)
                        } else {
                            showMsgFromResource(R.string.auth_error_already_connect_from_another_ip)
                        }
                }, { throwable ->
                    throwable.message?.let { errorMessage -> showMsg(errorMessage) }
                })
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
            repository.getItemsByInterval()
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
            repository.getItems()
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
        disposableGetItemsByInterval = null
    }

    fun startChooseFile() {
        viewState.chooseTorrentFileToAdd()
    }

    fun startManualRefresh() {
        flowOfManualUpdates.onNext(true)
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
            request = repository.startItem(torrent.id, torrent.type),
            doOnSuccess = { getItems() },
            onSuccess = { s -> showMsg(States.valueOf(s).title) })
    }

    private fun pauseItem(torrent: Torrent) {
        makeRequest(
            request = repository.pauseItem(torrent.id, torrent.type),
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
            request = repository.removeItem(torrent.id, torrent.type),
            doOnSuccess = { getItems() },
            onSuccess = { s -> showMsg(States.valueOf(s).title) })
    }

    fun startAllItems() {
        makeRequest(
            request = repository.startAllItems(),
            doOnSuccess = { getItems() },
            onSuccess = { s -> showMsg(States.valueOf(s).title) })
    }

    fun pauseAllItems() {
        makeRequest(
            request = repository.pauseAllItems(),
            doOnSuccess = { getItems() },
            onSuccess = { s -> showMsg(States.valueOf(s).title) })
    }

    fun clearAllCompletedItems() {
        makeRequest(
            request = repository.clearAllCompleteItems(),
            doOnSuccess = { getItems() },
            onSuccess = { s -> showMsg(States.valueOf(s).title) })
    }

    fun sendFile(stream: InputStream, dir: File) {
        val fileToSend =
            fileFromStream(stream, dir)
        val requestFile =
            RequestBody.create(MediaType.parse(Const.FILE_TYPE), fileToSend)

        val body = MultipartBody.Part.createFormData("file", fileToSend.name, requestFile)

        compositeDisposable.add(
            repository.sendTorrentFile(body)
                .doOnError { e ->
                    showMsg(e.message.toString())
                }
                .flatMap {
                    repository.confirmDownload(fileToSend.name)
                }
                .subscribe({ s ->
                    when {
                        s.contains(Const.ACK_SUCESS_RESPONSE) -> showMsgFromResource(R.string.success_action)
                        s.contains(Const.BT_EXIST_RESPONSE) -> showMsgFromResource(R.string.torrent_already_exist)
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
        viewState.showMsgFromResource(id)
    }
}