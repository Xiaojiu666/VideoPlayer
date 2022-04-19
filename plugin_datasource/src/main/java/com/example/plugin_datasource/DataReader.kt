package com.example.plugin_datasource


interface DataReader {
    fun read(buffer: ByteArray?, offset: Int, length: Int): Int
}