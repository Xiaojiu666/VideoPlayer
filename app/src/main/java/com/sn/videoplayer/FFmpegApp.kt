package com.sn.videoplayer

import android.app.Application
import com.sn.videoplayer.data.Config

class FFmpegApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Config.setFilePath(applicationContext.getExternalFilesDir("FFmpeg")!!.path + "/lake.mp4")
//        Config.setFilePath("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4")
    }
}