package com.fedorov.asusdownloader.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Torrent(
    var id: String = "",
    var name: String = "",
    var percent: Float = 0f,
    var volume: String = "",
    var status: String = "",
    var type: String = "",
    var timeOnline: String = "",
    var uploadSpeed: String = "",
    var downloadSpeed: String = "",
    var countPeers: String = "",
    var position:Int = 0
) :
    Parcelable {
    override fun toString(): String = String.format("%s %s %s", this.id, this.name, this.volume)
}