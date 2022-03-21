package com.sn.videoplayer.ffmpeg.demo

import android.view.Surface
import com.sn.videoplayer.PlayerControl

class FFmpegPlayer : PlayerControl() {

    override fun initVideoPlayer(surface: Surface, filePath: String): Int {
        return DemoNativeInterface.createPlayer(filePath, surface)
    }


    override fun start(playId: Int) {
        DemoNativeInterface.play(playId)
    }

    override fun stop(playId: Int) {
        DemoNativeInterface.pause(playId)
    }

    override fun onDestroy() {

    }

    fun getFFmpegInfo(): String? {
        return DemoNativeInterface.ffmpegInfo()
    }

    fun getVideoTime(playId: Int):String{
        return DemoNativeInterface.videoTime(playId)
    }

}