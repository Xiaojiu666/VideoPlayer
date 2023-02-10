package com.sn.videoplayer.setting

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.sn.videoplayer.MediaInfoManager
import com.sn.videoplayer.R
import com.sn.videoplayer.utils.Time
import kotlinx.android.synthetic.main.activity_setting_media_info.*
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.activity_video.toolbar


class SettingMediaInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_media_info)
        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar);//显示ToolBar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        val mediaInfo = MediaInfoManager.mediaInfo
        val audioInfo = MediaInfoManager.audioInfo

        if (mediaInfo != null){
            tv_video_duration.text = Time.formatSeconds(mediaInfo.duration)
            tv_video_width.text = mediaInfo.width.toString()
            tv_video_height.text = mediaInfo.height.toString()
            tv_video_bitRate.text =String.format( getString(R.string.media_info_kps),mediaInfo.bitRate)
            tv_video_frames.text = String.format( getString(R.string.media_info_frame),mediaInfo.frames)
        }

        if (audioInfo!=null){
            tv_audio_channels.text = audioInfo.channels.toString()
            tv_audio_sample_rate.text = String.format( getString(R.string.audio_info_sample),audioInfo.sample_rate)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item)
    }
}