package com.sn.videoplayer.ffmpeg.demo

import android.text.TextUtils
import android.util.Log
import android.view.Surface
import com.google.gson.Gson
import com.sn.videoplayer.MediaInfoManager

object DemoNativeInterface {

    external fun ffmpegInfo(): String? //    public static native String ffmpegInfo();

    external fun createPlayer(path: String, surface: Surface): Int

    external fun play(player: Int)

    external fun pause(player: Int)

    external fun setSeekTime(player: Int,time: Int): String

    external fun videoTotalTime(player: Int): Int

    external fun threadStart();

    external fun threadStop();

    external fun videoInfo(path: String): String

    external fun initMedia(path: String): Int

    external fun getMediaInfo(media: Int): String

    external fun generatePng(media: Int, path: String)


    fun playerInfoCallbackMsg(string: String) {
        Log.d("playerInfoCallback", "playerInfoCallbackMsg $string");
        playerInfoCallBack!!.playerInfo(string)
    }

    fun mediaInfoCallbackMsg(string: String) {
        Log.d("DemoNativeInterface", "mediaInfoCallbackMsg $string");
        if (TextUtils.isEmpty(string)) {
            return
        }
        val fromJson = Gson().fromJson(string, MediaInfo::class.java)
        MediaInfoManager.mediaInfo = fromJson
        mediaInfoCallBack!!.mediaInfoCallBack(fromJson)
    }

    fun audioInfoCallbackMsg(string: String) {
        Log.d("DemoNativeInterface", "audioInfoCallbackMsg $string");
        if (TextUtils.isEmpty(string)) {
            return
        }
        val fromJson = Gson().fromJson(string, AudioInfo::class.java)
        MediaInfoManager.audioInfo = fromJson
        mediaInfoCallBack!!.audioInfoCallBack(fromJson)
    }

    fun mediaInfoCallbackMsg(mediaInfo: MediaInfo) {
        Log.d("DemoNativeInterface", "mediaInfoCallbackMsg $mediaInfo");
//        mediaInfoCallBack!!.mediaInfoCallBack(mediaInfo)
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

    var mediaInfoCallBack: MediaInfoCallBack? = null

    var playerInfoCallBack: PlayerInfoCallBack? = null

//    @JvmName("setMediaInfoCallBack1")
//    fun setMediaInfoCallBack(mediaInfoCallBack: MediaInfoCallBack) {
//        this.mediaInfoCallBack = mediaInfoCallBack
//    }

    init {
        System.loadLibrary("native-lib")
    }


}