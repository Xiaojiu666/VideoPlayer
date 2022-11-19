package com.sn.videoplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.SurfaceHolder
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.sn.videoplayer.data.Config
import com.sn.videoplayer.ffmpeg.demo.DemoNativeInterface
import com.sn.videoplayer.ffmpeg.demo.FFmpegPlayer
import com.sn.videoplayer.ffmpeg.demo.MediaInfoCallBack
import com.sn.videoplayer.ffmpeg.demo.PlayerInfoCallBack
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.startActivity

class VideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        initView()
        val fFmpegPlayer = FFmpegPlayer()
//        val fFmpegInfo = fFmpegPlayer.getFFmpegInfo()
//
//        val videoInfo = DemoNativeInterface.initMedia(Config.FILE_NAME_LAKE)
//        val mediaInfo = DemoNativeInterface.getMediaInfo(videoInfo)

//        DemoNativeInterface.mediaInfoCallBack = (object : MediaInfoCallBack {
//            @SuppressLint("SetTextI18n")
//            override fun generatePngCallBack(imagePath: String) {
//                runOnUiThread {
//                    textView.text = mediaInfo + imagePath
//                }
//            }
//        })

        DemoNativeInterface.playerInfoCallBack = (object : PlayerInfoCallBack {
            override fun playerInfo(string: String) {
                textView.text = textView.text.toString() + string + "\n"
            }
        })

        // CoroutineScope（英文翻译：携程范围，即我们的携程体）
//        GlobalScope.launch {
//            DemoNativeInterface.generatePng(videoInfo, Config.FOLDER_PATH_IMAGE);
//        }

//        Thread(Runnable {
//            DemoNativeInterface.setProgress(PlayProgress {
//                runOnUiThread {
//                    seekBar.setCurrentTime(it.toInt())
//                }
//            })
//        }).start()
//
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
                var initVideoPlayer =
                    fFmpegPlayer.initVideoPlayer(holder.surface, Config.FILE_NAME_LAKE)
                Log.e(TAG, "initVideoPlayer : $initVideoPlayer")
                Thread(Runnable {
                    runOnUiThread {
//                        var videoTime = fFmpegPlayer.getVideoTime(initVideoPlayer)
//                        var videoTime = fFmpegPlayer.getVideoTotalTime(initVideoPlayer)
//                        Log.e(TAG, "videoTime : $videoTime")
//                        seekBar.setTotalTime(videoTime)
                    }
                }).start()
            }
        })
//
//
//
//        video_start.setOnClickListener {
//            fFmpegPlayer.start(initVideoPlayer)
//        }
//
//        video_stop.setOnClickListener {
//            fFmpegPlayer.stop(initVideoPlayer)
//        }
//        video_seek_after.setOnClickListener {
//            DemoNativeInterface.threadStart()
//        }
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.video_setting -> {
                    //https://www.jianshu.com/p/4a3ca2c315f2
                    startActivity<VideoSettingActivity>()
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    companion object {
        const val TAG = "VideoActivity"
    }
}