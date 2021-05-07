package com.sn.videoplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.sn.videoplayer.R;

public class VideoSeekBar extends LinearLayout {
    public VideoSeekBar(Context context) {
        this(context,null);
    }

    public VideoSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public VideoSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View inflate = View.inflate(context, R.layout.video_seek_bar, this);
    }
}
