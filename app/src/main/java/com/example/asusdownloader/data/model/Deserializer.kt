package com.example.asusdownloader.data.model

import java.util.regex.Pattern

fun deserialize(s: String): List<Torrent> {
    val result = mutableListOf<Torrent>()

    val m =
        Pattern.compile("\\[((\"[^,^\"]*\"),)*(\"[^,^\"]*\")]").matcher(s)
    while (m.find()) {
        val res = m.toMatchResult().group(0)
        val m2 =
            Pattern.compile("\"([^\"]*)\"").matcher(res)
        var counter = 0
        val item = Torrent()
        while (m2.find()) {
            val res2 = m2.toMatchResult().group(1)
            when (counter) {
                0 -> {
                    item.id = res2
                    try {
                        item.position = res2.drop(7).toInt()
                    }catch (e: NumberFormatException) {
                        item.position = 0
                    }

                }
                1 -> item.name = res2
                2 -> try {
                    item.percent = res2.toFloat() * 100
                } catch (e: NumberFormatException) {
                    item.percent = 0f
                }
                3 -> item.volume = res2
                4 -> item.status = res2
                5 -> item.type = res2
                6 -> item.timeOnline = res2
                7 -> item.downloadSpeed = res2
                8 -> item.uploadSpeed = res2
                9 -> item.countPeers = res2
            }
            counter++
        }
        result.add(item)
    }

    return result.sortedBy { it.position }
}