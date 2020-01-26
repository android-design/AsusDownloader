package com.fedorov.asusdownloader.ui.fragment.dashboard

interface DashboardCommunicator {
    fun onContextMenuClosed()
    fun pickFile()
    fun startAllItems()
    fun pauseAllItems()
    fun clearAllCompletedItems()
}