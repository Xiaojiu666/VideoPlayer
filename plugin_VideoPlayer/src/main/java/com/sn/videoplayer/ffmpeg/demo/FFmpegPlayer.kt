package com.sn.videoplayer.ffmpeg.demo

import com.sn.videoplayer.PlayerControl

class FFmpegPlayer : PlayerControl() {


    override fun start() {
    }

    override fun stop() {
    }

    override fun onDestroy() {
    }

    fun getFFmpegInfo(): String{
       return DemoNativeInterface.ffmpegInfo()
    }

}