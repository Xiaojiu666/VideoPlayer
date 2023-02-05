package com.sn.videoplayer

import android.view.Surface
import com.sn.videoplayer.ffmpeg.demo.FFmpegPlayer
import java.nio.file.Path
object VideoControlManager{

    private var ffmpegPlayer : PlayerControl? = null

    fun initPlayer(ffmpegPlayer : PlayerControl){
        this.ffmpegPlayer = ffmpegPlayer
    }

    fun playStart(){
        ffmpegPlayer!!.start()
    }

    fun playStop(){
        ffmpegPlayer!!.stop()
    }



}


