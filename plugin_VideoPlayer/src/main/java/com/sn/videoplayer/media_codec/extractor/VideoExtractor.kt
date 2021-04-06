package com.sn.videoplayer.media_codec.extractor

import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log

class VideoExtractor(filePath: String) : BaseExtractor(filePath) {

    /**
     * 视频通道索引
     */
    var mVideoTrack = -1

    var duration = 0L
    override fun getTrackType(): Int {
        return mVideoTrack
    }

    override fun getMediaFormat(): MediaFormat? {
        //【2.1，获取视频多媒体格式】
        Log.e(
            TAG,
            " getVideoFormat  getTrackCount " + mExtractor!!.trackCount
        )
        for (i in 0 until mExtractor!!.trackCount) {
            val mediaFormat = mExtractor!!.getTrackFormat(i)
            mExtractor!!.cachedDuration
            val mime = mediaFormat.getString(MediaFormat.KEY_MIME)
            if (mime!!.startsWith("video/")) {
                mVideoTrack = i
                break
            }
        }
        duration = mExtractor!!.getTrackFormat(mVideoTrack)!!.getLong(MediaFormat.KEY_DURATION)
        getFrameTime()
        return if (mVideoTrack >= 0) {
            mExtractor!!.getTrackFormat(mVideoTrack)
        } else {
            null
        }
    }

    fun getFrameTime() {
        var list = ArrayList<Long>()
        var step = 1000000L
        mExtractor!!.seekTo(duration / 3, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        val start: Long = mExtractor!!.getSampleTime()
        Log.e(TAG, "getFrameTime ${start.toString()}")
        Log.e(TAG, "getFrameTime ${duration.toString()}")
        mExtractor!!.advance()
        while (true) { //获取遍历步长
            if (mExtractor!!.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                step = Math.min(mExtractor!!.getSampleTime() - start, step);
                break;
            }
            mExtractor!!.advance();
        }
        list.add(0L);
        mExtractor!!.seekTo(step, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        var time = mExtractor!!.getSampleTime();
        Log.e(TAG, "getFrameTime ${time}")
        while (time < duration) { //获取关键帧时间戳列表
            var time_temp = mExtractor!!.getSampleTime();
            if (time_temp > list.get(list.size - 1)) {
                list.add(time_temp);
                time = time_temp;
            } else {
                time += step;
            }
            mExtractor!!.seekTo(time, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        }

        Log.e(TAG, "getFrameTime ${list.toString()}")
    }

}