package com.example.asusdownloader.presentation.view

import com.example.asusdownloader.data.model.Torrent
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface MainView : MvpView {
    fun showMsg(s: String)
    fun showMsgFromResouce(id: Int)

    fun updateListTorrents(items: List<Torrent>)

    fun chooseTorrentFileToAdd()

    fun stopRefreshAnimation()

}