package com.example.asusdownloader.presentation.view

import com.example.asusdownloader.data.model.Torrent
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface DashBoardView : MvpView {
    fun showMsg(s: String)
    fun showMsgFromResource(id: Int)
    fun updateListTorrents(items: List<Torrent>)
    fun chooseTorrentFileToAdd()
    fun stopRefreshAnimation()
}