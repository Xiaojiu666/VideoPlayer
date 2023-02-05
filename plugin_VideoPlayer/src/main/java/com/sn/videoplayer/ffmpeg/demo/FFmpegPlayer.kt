package com.sn.videoplayer.ffmpeg.demo

import android.view.Surface
import com.sn.videoplayer.PlayerControl

class FFmpegPlayer(surface: Surface,filePath: String) : PlayerControl() {

    var playId = -1;

    init {
        playId = DemoNativeInterface.createPlayer(filePath, surface)
    }

    override fun initVideoPlayer(surface: Surface, filePath: String): Int {
        return DemoNativeInterface.createPlayer(filePath, surface)
    }


    override fun start() {
        DemoNativeInterface.play(playId)
    }

    override fun stop() {
        DemoNativeInterface.pause(playId)
    }

    override fun onDestroy() {

    }

    fun getFFmpegInfo(): String? {
        return DemoNativeInterface.ffmpegInfo()
    }

//    fun getVideoTime(playId: Int):String{
//        return DemoNativeInterface.setSeekTime(playId)
//    }

    fun getVideoTotalTime(playId: Int):Int{
        return DemoNativeInterface.videoTotalTime(playId)
    }

}