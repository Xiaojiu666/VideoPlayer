package com.sn.videoplayer

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
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
    private var sfv: SurfaceView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        val fFmpegInfo = FFmpegPlayer().getFFmpegInfo()
        textView.text =fFmpegInfo;
    }


    companion object {
        const val TAG = "VideoActivity"
    }
}