package com.fedorov.asusdownloader.ui

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.fedorov.asusdownloader.settings.Const.Companion.TORRENT_STATUS_DOWNLOADING
import com.fedorov.asusdownloader.R
import com.fedorov.asusdownloader.data.model.Torrent
import kotlinx.android.synthetic.main.rv_item.view.*

class RvAdapter(val onContextMenuShow: ((Torrent) -> Unit)? = null) :
    RecyclerView.Adapter<RvAdapter.RvViewHolder>() {
    var items: List<Torrent> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_item, parent, false)
        return RvViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RvViewHolder, position: Int) =
        holder.bind(items[position])

    inner class RvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnCreateContextMenuListener {

        private lateinit var mTorrent: Torrent

        init {
            itemView.setOnCreateContextMenuListener(this)
        }

        fun bind(torrent: Torrent) = with(torrent) {
            mTorrent = torrent

            itemView.name.text =
                if (this.name.isNotEmpty()) this.name else itemView.context.getString(R.string.empty_torrent_name)
            itemView.volume.text = this.volume
            itemView.status.text = this.status
            itemView.countPeers.text =
                itemView.context.getString(R.string.count_peers, this.countPeers)
            itemView.downloadSpeed.text = this.downloadSpeed
            itemView.uploadSpeed.text = this.uploadSpeed
            itemView.progressBar.progress = this.percent.toInt()
        }

        override fun onCreateContextMenu(
            contextMenu: ContextMenu,
            view: View,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            onContextMenuShow?.invoke(mTorrent)

            contextMenu.setHeaderTitle(R.string.context_header_title)

            contextMenu.add(Menu.NONE, R.id.startMenuItem, Menu.NONE, R.string.startItem)
            contextMenu.getItem(0).isEnabled = isEnabledForStart(mTorrent.status)
            contextMenu.add(Menu.NONE, R.id.pauseMenuItem, Menu.NONE, R.string.pauseItem)
            contextMenu.getItem(1).isEnabled = !isEnabledForStart(mTorrent.status)
            contextMenu.add(Menu.NONE, R.id.removeMenuItem, Menu.NONE, R.string.removeItem)
        }

        private fun isEnabledForStart(status: String): Boolean {
            if (status.equals(TORRENT_STATUS_DOWNLOADING, true))
                return false

            return true
        }
    }
}