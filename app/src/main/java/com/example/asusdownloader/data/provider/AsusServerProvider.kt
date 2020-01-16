package com.example.asusdownloader.data.provider

import com.example.asusdownloader.Preferences
import com.example.asusdownloader.data.model.Torrent
import com.example.asusdownloader.data.model.deserialize
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

class AsusServerProvider : RemoteDataProvider {

    private val restApi: AsusServiceRestApi

    init {
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        val cookieJar: CookieJar = JavaNetCookieJar(cookieManager)
        val builder = OkHttpClient.Builder()
        builder.cookieJar(cookieJar)
        val client = builder.build()

        // TODO Replace with dagger.
        val mRetrofit = Retrofit.Builder()
            .baseUrl("http://${Preferences.address}:${Preferences.port}/")
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        restApi = mRetrofit.create(AsusServiceRestApi::class.java)
    }

    override fun auth(): Single<Response<String>> = restApi.auth(Preferences.login, Preferences.password)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

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
        Observable.interval(0, 10, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .flatMap<String> {
                restApi.getItems()
            }
            .map<List<Torrent>> { s: String ->
                deserialize(s)
            }
            .observeOn(AndroidSchedulers.mainThread())

    override fun setItemStart(itemId: String, itemType: String): Single<String> =
        restApi.startItem(itemId, itemType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun setItemPause(itemId: String, itemType: String): Single<String> =
        restApi.pauseItem(itemId, itemType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun setItemRemove(itemId: String, itemType: String): Single<String> =
        restApi.removeItem(itemId, itemType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun setStartAllItems(): Single<String> =
        restApi.startAllItems()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun setPauseAllItems(): Single<String> =
        restApi.pauseAllItems()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun setClearAllCompleteItems(): Single<String> =
        restApi.clearAllCompleteItems()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun sendLinkToTorrent(link: String): Single<String> =
        restApi.sendLink(link)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun sendTorrentFile(file: MultipartBody.Part): Single<String> =
        restApi.upload(file)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun confirmDownload(fileName: String): Single<String> =
        restApi.confirmDownload(fileName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}