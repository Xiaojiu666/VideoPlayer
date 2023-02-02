package com.sn.videoplayer.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.sn.videoplayer.R
import com.sn.videoplayer.utils.Time.formatSeconds
import kotlinx.android.synthetic.main.video_seek_bar.view.*
import java.time.Duration

class VideoSeekBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : LinearLayout(context, attrs, defStyleAttr) {
    private val tvSeekBarTotalTime: TextView
    val videoSeekBar: SeekBar

    init {
        val rootView = inflate(context, R.layout.video_seek_bar, this)
        tvSeekBarTotalTime = rootView.findViewById<View>(R.id.tv_seekbar_total_time) as TextView
        videoSeekBar = rootView.findViewById(R.id.seek_time_progress) as SeekBar
    }

    fun setTotalTime(totalTime: String?) {
        tvSeekBarTotalTime.text = totalTime
    }

    fun setTotalTime(totalTime: Int) {
        seek_time_progress.max = totalTime
        tvSeekBarTotalTime.text = formatSeconds(totalTime)
    }

    fun setCurrentTime(double: Int) {
        seek_time_progress.progress = double;
        Log.d("setCurrentTime", formatSeconds(double))
        tv_seek_current_time.text = formatSeconds(double)
    }


}