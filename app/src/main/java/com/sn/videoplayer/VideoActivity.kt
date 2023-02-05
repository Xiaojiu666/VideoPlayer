package com.sn.videoplayer

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.sn.videoplayer.data.Config
import com.sn.videoplayer.ffmpeg.demo.*
import com.sn.videoplayer.setting.VideoSettingActivity
import com.sn.videoplayer.view.VideoPlaySurfaceView
import com.sn.videoplayer.worker.CopyFileWork
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.activity_video.view.*
import org.jetbrains.anko.startActivity

class VideoActivity : AppCompatActivity() {
    var defaultTime = 0
    var seekStatus = false
    var videoFilePath = ""
    private lateinit var  videoPlayView : VideoPlaySurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        initData()
        initView()

        DemoNativeInterface.mediaInfoCallBack = (object : MediaInfoCallBack {
            override fun mediaInfoCallBack(mediaInfo: MediaInfo) {
                runOnUiThread {
                    video_seek_bar.setTotalTime(mediaInfo.duration)
                }
            }
        })

        DemoNativeInterface.playerInfoCallBack = (object : PlayerInfoCallBack {
            override fun playerInfo(string: String) {
                runOnUiThread {
                    textView.text = textView.text.toString() + string + "\n"
                }
            }
        })

        DemoNativeInterface.mPlayProgress = (object : PlayProgress {
            override fun progress(progress: Double) {
                if (seekStatus) {
                    return
                }
                runOnUiThread {
                    video_seek_bar.setCurrentTime(progress.toInt())
                }
            }

        })
        var initVideoPlayer = 0;
//        sfv!!.holder!!.addCallback(object : SurfaceHolder.Callback {
//            override fun surfaceChanged(
//                holder: SurfaceHolder,
//                format: Int,
//                width: Int,
//                height: Int
//            ) {
//            }
//
//            override fun surfaceDestroyed(holder: SurfaceHolder) {
//            }
//
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                initVideoPlayer =
//                    fFmpegPlayer.initVideoPlayer(holder.surface, Config.PLAY_FILE_PATH)
//                Log.e(TAG, "initVideoPlayer : $initVideoPlayer")
//                Thread(Runnable {
//                    runOnUiThread {
////                        var videoTime = fFmpegPlayer.getVideoTime(initVideoPlayer)
////                        var videoTime = fFmpegPlayer.getVideoTotalTime(initVideoPlayer)
////                        Log.e(TAG, "videoTime : $videoTime")
////                        seekBar.setTotalTime(videoTime)
//                    }
//                }).start()
//            }
//        })
////
//
//
        video_start.setOnClickListener {
            videoPlayView.start()
        }

        video_stop.setOnClickListener {
            videoPlayView.stop()
        }
        video_seek_after.setOnClickListener {
        }

        video_seek_bar.videoSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                defaultTime = progress
                video_seek_bar.setCurrentTime(progress.toInt())
                Log.d(TAG, "progress $progress")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekStatus = true
                Log.d(TAG, "progress onStartTrackingTouch")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                DemoNativeInterface.setSeekTime(initVideoPlayer, defaultTime)
                seekStatus = false
                Log.d(TAG, "progress onStopTrackingTouch")
            }

        })
    }

    private fun initData() {
        videoFilePath =  intent.getStringExtra("videoFilePath")!!
    }

    private fun initView() {
        videoPlayView = findViewById(R.id.sfv)
        videoPlayView.setFile(videoFilePath)
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