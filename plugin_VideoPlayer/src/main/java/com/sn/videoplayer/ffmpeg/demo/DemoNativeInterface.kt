package com.sn.videoplayer.ffmpeg.demo

import android.util.Log
import android.view.Surface

object DemoNativeInterface {

    external fun ffmpegInfo(): String? //    public static native String ffmpegInfo();

    external fun createPlayer(path: String, surface: Surface): Int

    external fun play(player: Int)

    external fun pause(player: Int)

    external fun videoTime(player: Int): String

    external fun threadStart();

    external fun threadStop();

    fun nativeCallback(int: Int) {
        Log.d("DemoNativeInterface", "nativeCallback $int");
    }

    init {
        System.loadLibrary("native-lib")
    }
}