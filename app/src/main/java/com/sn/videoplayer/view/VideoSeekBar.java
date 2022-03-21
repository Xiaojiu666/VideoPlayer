package com.sn.videoplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sn.videoplayer.R;

public class VideoSeekBar extends LinearLayout {

    private TextView tvSeekBarTotalTime;
    public VideoSeekBar(Context context) {
        this(context,null);
    }

    public VideoSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public VideoSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView= View.inflate(context, R.layout.video_seek_bar, this);
        tvSeekBarTotalTime =  (TextView) findViewById(R.id.tv_seekbar_total_time);
    }

    public void setTotalTime(String totalTime) {
        tvSeekBarTotalTime.setText(totalTime);
    }
}
