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

}