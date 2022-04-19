package com.example.plugin_datasource

import android.net.Uri
import android.text.TextUtils
import java.io.FileNotFoundException
import java.io.IOException
import java.io.RandomAccessFile
import java.lang.RuntimeException

class FileDataSource : DataSource {
    private lateinit var uri: Uri
    private lateinit var file: RandomAccessFile
    private var bytesRemaining: Long = 0
    private var opened = false

    class Factory : DataSource.Factory {


        override fun createDataSource(): FileDataSource {
            val dataSource: FileDataSource = FileDataSource()
            return dataSource
        }
    }

    override fun open(dataSpec: DataSpec): Long {
        this.uri = dataSpec.uri!!
//        transferInitializing(dataSpec)
        this.file = openLocalFile(uri)
        file.seek(dataSpec.position)
        bytesRemaining =
            if (dataSpec.length == -1L) file.length() - dataSpec.position else dataSpec.length
        opened = true
//        transferStarted(dataSpec)
        return bytesRemaining
    }


    override fun close() {
        TODO("Not yet implemented")
    }

    override fun read(buffer: ByteArray?, offset: Int, length: Int): Int {
        return if (length == 0) {
            0
        } else if (bytesRemaining == 0L) {
            -1
        } else {
            val bytesRead: Int
            bytesRead =
                file.read(
                    buffer, offset,
                    Math.min(bytesRemaining, length.toLong()).toInt()
                )

            if (bytesRead > 0) {
                bytesRemaining -= bytesRead.toLong()
//                bytesTransferred(bytesRead)
            }
            bytesRead
        }
    }

    private fun openLocalFile(uri: Uri): RandomAccessFile {
        return RandomAccessFile(uri.path, "r")
    }
}