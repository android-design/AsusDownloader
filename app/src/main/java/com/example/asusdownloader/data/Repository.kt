package com.example.asusdownloader.data

import com.example.asusdownloader.data.model.Torrent
import com.example.asusdownloader.data.provider.AsusServerProvider
import com.example.asusdownloader.data.provider.RemoteDataProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Response


object Repository {
    private val repo: RemoteDataProvider by lazy { AsusServerProvider() }

    fun auth(): Single<Response<String>> = repo.auth()
    fun logout():Completable = repo.logout()
    fun getItems(): Observable<List<Torrent>> = repo.getItems()
    fun getItemsByInterval(): Observable<List<Torrent>> = repo.getItemsByInterval()
    fun startItem(itemId: String, itemType: String): Single<String> = repo.setItemStart(itemId, itemType)
    fun pauseItem(itemId: String, itemType: String): Single<String> = repo.setItemPause(itemId, itemType)
    fun removeItem(itemId: String, itemType: String): Single<String> = repo.setItemRemove(itemId, itemType)
    fun startAllItems(): Single<String> = repo.setStartAllItems()
    fun pauseAllItems(): Single<String> = repo.setPauseAllItems()
    fun clearAllCompleteItems(): Single<String> = repo.setClearAllCompleteItems()
    fun sendTorrentFile(file: MultipartBody.Part):Single<String> = repo.sendTorrentFile(file)
    fun confirmDownload(fileName: String):Single<String> = repo.confirmDownload(fileName)
}