package com.sn.videoplayer

import android.app.Application
import com.sn.videoplayer.data.Config

class FFmpegApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Config.setFilePath(applicationContext.getExternalFilesDir("FFmpeg")!!.path + "/lake.mp4")
    }
}