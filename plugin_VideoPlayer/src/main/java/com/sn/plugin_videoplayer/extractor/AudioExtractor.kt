package com.sn.plugin_videoplayer.extractor

import android.media.MediaFormat
import android.util.Log

class AudioExtractor(filePath: String) : BaseExtractor(filePath) {

    /**
     * 音频通道索引
     */
    private var mAudioTrack = -1

    override fun getTrackType(): Int {
        Log.i(TAG, " mAudioTrack $mAudioTrack")
        return mAudioTrack
    }

    override fun getMediaFormat(): MediaFormat? {
        Log.e(TAG,
            " getVideoFormat  getTrackCount " + mExtractor!!.trackCount
        )
        for (i in 0 until mExtractor!!.trackCount) {
            val mediaFormat = mExtractor!!.getTrackFormat(i)
            val mime = mediaFormat.getString(MediaFormat.KEY_MIME)
            if (mime!!.startsWith("audio/")) {
                mAudioTrack = i
                break
            }
        }
        return if (mAudioTrack >= 0) {
            mExtractor!!.getTrackFormat(mAudioTrack)
        } else {
            null
        }
    }
}