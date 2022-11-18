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

    external fun getMediaInfo(media: Int): String

    external fun generatePng(media: Int, path: String)

    fun generatePngCallback(string: String) {
        Log.d("DemoNativeInterface", "generatePngCallback $string");
        if (mediaInfoCallBack != null){
            mediaInfoCallBack!!.generatePngCallBack(string)
        }
    }

    fun nativeCallback(double: Double) {
        Log.d("DemoNativeInterface", "nativeCallback $double");
        mPlayProgress.progress(double)
    }

    fun generatePngCallback(double: Int) {
        Log.d("DemoNativeInterface", "nativeCallback $double");
    }

    fun generatePngCallback(double: Double) {
        Log.d("DemoNativeInterface", "nativeCallback $double");
    }

    lateinit var mPlayProgress: PlayProgress;

    fun setProgress(playProgress: PlayProgress) {
        mPlayProgress = playProgress
    }

    var mediaInfoCallBack: MediaInfoCallBack? = null;

    @JvmName("setMediaInfoCallBack1")
    fun setMediaInfoCallBack(mediaInfoCallBack: MediaInfoCallBack){
        this.mediaInfoCallBack = mediaInfoCallBack
    }

    init {
        System.loadLibrary("native-lib")
    }
}