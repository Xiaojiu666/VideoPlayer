package com.sn.videoplayer

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.sn.videoplayer.data.Config
import com.sn.videoplayer.ffmpeg.demo.DemoNativeInterface
import com.sn.videoplayer.ffmpeg.demo.FFmpegPlayer
import com.sn.videoplayer.media_codec.Frame
import com.sn.videoplayer.media_codec.decoder.AudioDecoder
import com.sn.videoplayer.media_codec.decoder.BaseDecoder
import com.sn.videoplayer.media_codec.decoder.IDecoderStateListener
import com.sn.videoplayer.media_codec.decoder.VideoDecoder
import kotlinx.android.synthetic.main.activity_video.*
import java.util.concurrent.Executors

class VideoActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        val fFmpegPlayer = FFmpegPlayer()
        val fFmpegInfo = fFmpegPlayer.getFFmpegInfo()
        var initVideoPlayer = 0
        textView.text =fFmpegInfo
        sfv!!.holder!!.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                initVideoPlayer  = fFmpegPlayer.initVideoPlayer(holder.surface, Config.FILE_PATH)
                Log.e(TAG,"initVideoPlayer : $initVideoPlayer")
            }
        })


        video_start.setOnClickListener {
            fFmpegPlayer.start(initVideoPlayer)
        }

    }

    companion object {
        const val TAG = "VideoActivity"
    }
}