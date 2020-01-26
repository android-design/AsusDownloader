package com.fedorov.asusdownloader.data

import com.fedorov.asusdownloader.data.model.Torrent
import com.fedorov.asusdownloader.data.provider.RemoteDataProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(private val repo: RemoteDataProvider) {
    fun auth(): Single<Response<String>> = repo.auth()
    fun logout(): Completable = repo.logout()
    fun getItems(): Observable<List<Torrent>> = repo.getItems()
    fun getItemsByInterval(): Observable<List<Torrent>> = repo.getItemsByInterval()
    fun startItem(itemId: String, itemType: String): Single<String> =
        repo.setItemStart(itemId, itemType)

    fun pauseItem(itemId: String, itemType: String): Single<String> =
        repo.setItemPause(itemId, itemType)

    fun removeItem(itemId: String, itemType: String): Single<String> =
        repo.setItemRemove(itemId, itemType)

    fun startAllItems(): Single<String> = repo.setStartAllItems()
    fun pauseAllItems(): Single<String> = repo.setPauseAllItems()
    fun clearAllCompleteItems(): Single<String> = repo.setClearAllCompleteItems()
    fun sendTorrentFile(file: MultipartBody.Part): Single<String> = repo.sendTorrentFile(file)
    fun confirmDownload(fileName: String): Single<String> = repo.confirmDownload(fileName)
}