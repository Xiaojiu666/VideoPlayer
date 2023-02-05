package com.sn.videoplayer

import android.view.Surface

abstract class  PlayerControl {

    abstract fun initVideoPlayer(surface: Surface,filePath:String): Int

    abstract fun start()

    abstract fun stop()

    abstract fun onDestroy()

}