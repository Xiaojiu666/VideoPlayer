package com.sn.videoplayer.ffmpeg.demo

import android.util.Log
import android.view.Surface

object DemoNativeInterface {

    external fun ffmpegInfo(): String? //    public static native String ffmpegInfo();

    external fun createPlayer(path: String, surface: Surface): Int

    external fun play(player: Int)

    external fun pause(player: Int)

    external fun videoTime(player: Int): String

    external fun videoTotalTime(player: Int): Int

    external fun threadStart();

    external fun threadStop();

    external fun videoInfo(path: String): String

    external fun initMedia(path: String): Int

    external fun getMediaInfo(media: Int):String


    fun nativeCallback(double: Double) {
        Log.d("DemoNativeInterface", "nativeCallback $double");
        mPlayProgress.progress(double)
    }

    lateinit var mPlayProgress: PlayProgress;

    fun setProgress(playProgress: PlayProgress) {
        mPlayProgress = playProgress
    }

    init {
        System.loadLibrary("native-lib")
    }
}