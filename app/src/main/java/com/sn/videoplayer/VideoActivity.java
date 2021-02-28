package com.sn.videoplayer;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.sn.plugin_videoplayer.Frame;
import com.sn.plugin_videoplayer.decoder.AudioDecoder;
import com.sn.plugin_videoplayer.decoder.BaseDecoder;
import com.sn.plugin_videoplayer.decoder.IDecoderStateListener;
import com.sn.plugin_videoplayer.decoder.VideoDecoder;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener, IDecoderStateListener {

    private SurfaceView sfv;

    public static final String TAG = "VideoActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initView();
        initPlayer();
       // initThread();
    }

    private void initThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "开始run" + "");
                while (true) {
                    Log.e(TAG, System.currentTimeMillis() + "");
                    //waitDecode();
                    Log.e(TAG, "开始解锁" + "");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private Button videoStart, videoStop, videoRest;

    private void initView() {
        sfv = (SurfaceView) findViewById(R.id.sfv);
        videoStart = (Button) findViewById(R.id.video_start);
        videoStop = (Button) findViewById(R.id.video_stop);
        videoRest = (Button) findViewById(R.id.video_rest);
        videoStart.setOnClickListener(this);
        videoStop.setOnClickListener(this);
        videoRest.setOnClickListener(this);
    }

    private VideoDecoder videoDecoder;
    private AudioDecoder audioDecoder;

    private void initPlayer() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lake.mp4";
        File file = new File(path);
//        if (!file.exists()) {
////            file.mkdir();
//        }
        //创建线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(2);

        //创建视频解码器
        videoDecoder = new VideoDecoder(path, sfv, null);
        threadPool.execute(videoDecoder);
        //创建音频解码器
        audioDecoder = new AudioDecoder(path);
        threadPool.execute(audioDecoder);


        audioDecoder.setStateListener(this);
        videoDecoder.setStateListener(this);

//
//        MP4Repack repack = new MP4Repack(path);
//        repack.start();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.video_start) {
            //开启播放
            videoDecoder.goOn();
            audioDecoder.goOn();
        } else if (id == R.id.video_stop) {
            videoDecoder.stop();
            audioDecoder.stop();
        } else if (id == R.id.video_rest) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoDecoder.release();
        audioDecoder.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoDecoder.release();
        audioDecoder.release();
    }

    @Override
    public void decoderPrepare(BaseDecoder decodeJob) {

    }

    @Override
    public void decoderReady(BaseDecoder decodeJob) {

    }

    @Override
    public void decoderRunning(BaseDecoder decodeJob) {

    }

    @Override
    public void decoderPause(BaseDecoder decodeJob) {

    }

    @Override
    public void decodeOneFrame(BaseDecoder decodeJob, Frame frame) {

    }

    @Override
    public void decoderFinish(BaseDecoder decodeJob) {

    }

    @Override
    public void decoderDestroy(BaseDecoder decodeJob) {

    }

    @Override
    public void decoderError(BaseDecoder decodeJob, String msg) {

    }
}
