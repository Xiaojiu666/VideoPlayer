package com.sn.videoplayer

import android.view.Surface

abstract class  PlayerControl {

    abstract fun initVideoPlayer(surface: Surface,filePath:String): Int

    abstract fun start(playId: Int)

    abstract fun stop(playId: Int)

    abstract fun onDestroy()

}