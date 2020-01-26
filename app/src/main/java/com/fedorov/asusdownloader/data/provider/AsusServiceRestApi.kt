package com.fedorov.asusdownloader.data.provider

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*


interface AsusServiceRestApi {
    @GET("/downloadmaster/check.asp?flag=")
    fun auth(
        @Query("login_username") login: String, @Query(
            "login_passwd"
        ) password: String?
    ): Single<retrofit2.Response<String>>

    @GET("/downloadmaster/Logout.asp")
    fun logout(
    ): Completable

    @GET("/downloadmaster/dm_print_status.cgi?action_mode=All")
    fun getItems(): Observable<String>

    // TODO make one command.
    @GET("/downloadmaster/dm_apply.cgi?action_mode=DM_CTRL&dm_ctrl=start_all&download_type=ALL")
    fun startAllItems(): Single<String>

    @GET("/downloadmaster/dm_apply.cgi?action_mode=DM_CTRL&dm_ctrl=pause_all&download_type=ALL")
    fun pauseAllItems(): Single<String>

    @GET("/downloadmaster/dm_apply.cgi?action_mode=DM_CTRL&dm_ctrl=clear&download_type=ALL")
    fun clearAllCompleteItems(): Single<String>

    @GET("/downloadmaster/dm_apply.cgi?action_mode=DM_CTRL&dm_ctrl=start")
    fun startItem(
        @Query("task_id") itemId: String, @Query(
            "download_type"
        ) itemType: String
    ): Single<String>

    //TODO make one method.
    @GET("/downloadmaster/dm_apply.cgi?action_mode=DM_CTRL&dm_ctrl=paused")
    fun pauseItem(
        @Query("task_id") itemId: String, @Query(
            "download_type"
        ) itemType: String
    ): Single<String>

    @GET("/downloadmaster/dm_apply.cgi?action_mode=DM_CTRL&dm_ctrl=cancel")
    fun removeItem(
        @Query("task_id") itemId: String, @Query(
            "download_type"
        ) itemType: String
    ): Single<String>

    @GET("/downloadmaster/dm_apply.cgi?action_mode=DM_ADD&download_type=5&again=no")
    fun sendLink(
        @Query("usb_dm_url") link: String
    ): Single<String>

    @Multipart
    @POST("/downloadmaster/dm_uploadbt.cgi")
    fun upload(
        @Part file: MultipartBody.Part?
    ): Single<String>

    @GET("/downloadmaster/dm_uploadbt.cgi?&download_type=All")
    fun confirmDownload(
        @Query("filename") fileName: String
    ): Single<String>
}