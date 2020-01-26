package com.fedorov.asusdownloader.data.provider

import com.fedorov.asusdownloader.settings.Preferences
import com.fedorov.asusdownloader.data.model.Torrent
import com.fedorov.asusdownloader.data.model.deserialize
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AsusServerProvider @Inject constructor(val retrofit: Retrofit, val preferences: Preferences) : RemoteDataProvider {

    private val restApi: AsusServiceRestApi = retrofit.create(AsusServiceRestApi::class.java)

    override fun auth(): Single<Response<String>> =
        restApi.auth(preferences.login, preferences.password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun test(){
    }

    override fun logout(): Completable =
        restApi.logout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun getItems(): Observable<List<Torrent>> = restApi.getItems()
        .subscribeOn(Schedulers.io())
        .map { s: String ->
            deserialize(s)
        }
        .observeOn(AndroidSchedulers.mainThread())


    override fun getItemsByInterval(): Observable<List<Torrent>> =
        Observable.interval(0, preferences.delay, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .flatMap<String> {
                restApi.getItems()
            }
            .map<List<Torrent>> { s: String ->
                deserialize(s)
            }
            .observeOn(AndroidSchedulers.mainThread())

    override fun setItemStart(
        itemId: String,
        itemType: String
    ): Single<String> =
        restApi.startItem(itemId, itemType).subscribeAndObserve()

    override fun setItemPause(
        itemId: String,
        itemType: String
    ): Single<String> =
        restApi.pauseItem(itemId, itemType).subscribeAndObserve()

    override fun setItemRemove(
        itemId: String,
        itemType: String
    ): Single<String> =
        restApi.removeItem(itemId, itemType).subscribeAndObserve()

    override fun setStartAllItems(): Single<String> =
        restApi.startAllItems().subscribeAndObserve()

    override fun setPauseAllItems(): Single<String> =
        restApi.pauseAllItems().subscribeAndObserve()

    override fun setClearAllCompleteItems(): Single<String> =
        restApi.clearAllCompleteItems().subscribeAndObserve()

    override fun sendLinkToTorrent(link: String): Single<String> =
        restApi.sendLink(link).subscribeAndObserve()

    override fun sendTorrentFile(file: MultipartBody.Part): Single<String> =
        restApi.upload(file).subscribeAndObserve()

    override fun confirmDownload(fileName: String): Single<String> =
        restApi.confirmDownload(fileName).subscribeAndObserve()

    private fun Single<String>.subscribeAndObserve(): Single<String> =
        this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}