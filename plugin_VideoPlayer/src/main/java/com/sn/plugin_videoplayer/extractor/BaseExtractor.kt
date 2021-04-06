package com.sn.plugin_videoplayer.extractor

import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.IOException
import java.nio.ByteBuffer

/**
 *  音视频信息提取器，主要用于分离音视频数据获取音/视频 数据
 *  对MediaExtractor 二次包装
 */
abstract class BaseExtractor(private val filePath : String) :
    IExtractor {
    /**
     * 音视频分离器
     */
    var mExtractor: MediaExtractor? =null

    val TAG = "BaseExtractor"

    /**
     * 当前帧时间戳
     */
    private var mCurSampleTime: Long? = null

    /**
     * 开始解码时间点
     */
    private var mStartPos: Long? = null

    /**
     * 当前帧标志
     */
    private var mCurSampleFlag = 0

    init {
        init(filePath)
    }

    private fun init(path: String) {
        //【1，初始化】
        try {
            mExtractor = MediaExtractor()
            mExtractor?.setDataSource(path)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    override fun getFormat(): MediaFormat? {
        return getMediaFormat()
    }

    override fun readBuffer(byteBuffer: ByteBuffer?): Int {
        //3，提取数据
        byteBuffer!!.clear()
        mExtractor!!.selectTrack(getTrackType())
        val readSampleCount = mExtractor!!.readSampleData(byteBuffer, 0)
        if (readSampleCount < 0) {
            return -1
        }
        mCurSampleTime = mExtractor!!.sampleTime

        mCurSampleFlag = mExtractor!!.sampleFlags
        mExtractor!!.advance() //提前到下一个采样数据，预加载 如果false 流结束
        return readSampleCount
    }

    override fun getCurrentTimestamp(): Long? {
        return mCurSampleTime!!
    }

    override fun getSampleFlag(): Int {
        return mCurSampleFlag
    }

    override fun seek(pos: Long): Long? {
        mExtractor!!.seekTo(pos, MediaExtractor.SEEK_TO_PREVIOUS_SYNC)
        return mExtractor!!.sampleTime
    }

    override fun setStartPos(pos: Long?) {
        mStartPos = pos
    }

    override fun stop() {
        //【4，释放提取器】
        mExtractor!!.release()
        mExtractor = null
    }





    abstract fun getTrackType(): Int

    abstract fun getMediaFormat(): MediaFormat?
}