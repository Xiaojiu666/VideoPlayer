package com.sn.videoplayer

import android.app.Application
import android.util.Log
import androidx.lifecycle.Observer

import com.sn.videoplayer.data.Config
import com.sn.videoplayer.worker.CopyFileWork

class FFmpegApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Config.setAppPath(applicationContext.getExternalFilesDir("FFmpeg")!!.path )
//        Config.setFilePath("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4")
    }


}