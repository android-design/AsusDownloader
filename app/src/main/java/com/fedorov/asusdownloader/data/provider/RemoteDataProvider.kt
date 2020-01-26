package com.fedorov.asusdownloader.data.provider

import com.fedorov.asusdownloader.data.model.Torrent
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Response

interface RemoteDataProvider {
    fun auth(): Single<Response<String>>
    fun logout(): Completable
    fun getItems(): Observable<List<Torrent>>
    fun getItemsByInterval(): Observable<List<Torrent>>
    fun setItemStart(itemId: String, itemType: String): Single<String>
    fun setItemPause(itemId: String, itemType: String): Single<String>
    fun setItemRemove(itemId: String, itemType: String): Single<String>
    fun setStartAllItems(): Single<String>
    fun setPauseAllItems(): Single<String>
    fun setClearAllCompleteItems(): Single<String>
    fun sendLinkToTorrent(link: String): Single<String>
    fun sendTorrentFile(file: MultipartBody.Part): Single<String>
    fun confirmDownload(fileName:String): Single<String>
}