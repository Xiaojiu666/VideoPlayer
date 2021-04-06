package com.sn.videoplayer.media_codec.extractor

import android.media.MediaFormat
import java.nio.ByteBuffer

interface IExtractor {
    /**
     * 获取音视频格式参数
     * 由于音/视频 有区分， 通过抽象方法由子类实现
     */
    fun getFormat() :MediaFormat?

    /**
     * 读取音视频数据
     */
    fun readBuffer(byteBuffer: ByteBuffer?): Int

    /**
     * 获取当前帧时间
     */
    fun getCurrentTimestamp(): Long?

    fun getSampleFlag(): Int

    /**
     * Seek到指定位置，并返回实际帧的时间戳
     */
    fun seek(pos: Long): Long?

    fun setStartPos(pos: Long?)

    /**
     * 停止读取数据
     */
    fun stop()

}