package com.fedorov.asusdownloader.presentation

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

fun fileFromStream(inputStream: InputStream, cacheDir: File): File {
    val outputFile = File(cacheDir, UUID.randomUUID().toString())

    inputStream.use { input ->
        val outputStream = FileOutputStream(outputFile)
        outputStream.use { output ->
            val buffer = ByteArray(4 * 1024) // buffer size
            while (true) {
                val byteCount = input.read(buffer)
                if (byteCount < 0) break
                output.write(buffer, 0, byteCount)
            }
            output.flush()
        }
    }
    return outputFile
}