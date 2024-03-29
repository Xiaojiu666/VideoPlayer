package com.sn.videoplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sn.videoplayer.data.Config
import com.sn.videoplayer.media_codec.MediaCodecPlayer
import kotlinx.android.synthetic.main.activity_video.*

class VideoHardActivity : AppCompatActivity() {

    lateinit var mediaCodecPlayer: MediaCodecPlayer;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        mediaCodecPlayer = MediaCodecPlayer(sfv, Config.FOLDER_PATH_VIDEO)
        initView()
    }

    private fun initView() {
        video_start.setOnClickListener {
            mediaCodecPlayer.start()
        }

        video_stop.setOnClickListener {
            mediaCodecPlayer.stop()
        }


    }
}