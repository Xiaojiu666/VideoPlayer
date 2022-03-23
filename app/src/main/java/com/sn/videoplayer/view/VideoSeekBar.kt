package com.sn.videoplayer.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.sn.videoplayer.R
import kotlinx.android.synthetic.main.video_seek_bar.view.*
import java.time.Duration

class VideoSeekBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : LinearLayout(context, attrs, defStyleAttr) {
    private val tvSeekBarTotalTime: TextView


    init {
        val rootView = inflate(context, R.layout.video_seek_bar, this)
        tvSeekBarTotalTime = findViewById<View>(R.id.tv_seekbar_total_time) as TextView
    }

    fun setTotalTime(totalTime: String?) {
        tvSeekBarTotalTime.text = totalTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCurrentTime(double: Int) {
        seek_time_progress.progress = double;
        val duration: Duration = Duration.ofSeconds(double.toLong())
        Log.d("setCurrentTime",formatSeconds(double))
        tv_seek_current_time.text = formatSeconds(double)
    }

    fun formatSeconds(timeInSeconds: Int): String {
        val hours = timeInSeconds / 3600
        val secondsLeft = timeInSeconds - hours * 3600
        val minutes = secondsLeft / 60
        val seconds = secondsLeft - minutes * 60
        var formattedTime = ""
        if (hours < 10) formattedTime += "0"
        formattedTime += "$hours:"
        if (minutes < 10) formattedTime += "0"
        formattedTime += "$minutes:"
        if (seconds < 10) formattedTime += "0"
        formattedTime += seconds
        return formattedTime
    }

}