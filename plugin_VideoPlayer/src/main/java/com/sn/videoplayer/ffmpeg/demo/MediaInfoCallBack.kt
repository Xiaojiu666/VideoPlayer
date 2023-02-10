package com.sn.videoplayer.ffmpeg.demo

/**
 *  媒体信息信息回调
 */
interface MediaInfoCallBack {

    fun mediaInfoCallBack(mediaInfo: MediaInfo)

    fun audioInfoCallBack(audioInfo: AudioInfo)

}