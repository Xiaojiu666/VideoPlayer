package com.sn.videoplayer.ffmpeg.demo

import android.view.Surface

object DemoNativeInterface {
    external fun ffmpegInfo(): String? //    public static native String ffmpegInfo();

    external fun createPlayer(path: String, surface: Surface): Int

    external fun play(player: Int)

    external fun pause(player: Int)

    init {
        System.loadLibrary("native-lib")
    }
}