package com.sn.videoplayer

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.sn.plugin_videoplayer.Frame
import com.sn.plugin_videoplayer.decoder.AudioDecoder
import com.sn.plugin_videoplayer.decoder.BaseDecoder
import com.sn.plugin_videoplayer.decoder.IDecoderStateListener
import com.sn.plugin_videoplayer.decoder.VideoDecoder
import kotlinx.android.synthetic.main.activity_video.*
import java.util.concurrent.Executors

class VideoActivity : AppCompatActivity(), View.OnClickListener,
    IDecoderStateListener {
    private var sfv: SurfaceView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        initView()
        initPlayer()
    }


    private var videoStart: Button? = null
    private var videoStop: Button? = null
    private var videoRest: Button? = null
    private fun initView() {
        sfv = findViewById<View>(R.id.sfv) as SurfaceView
        video_seek_before.setOnClickListener {
            videoDecoder?.seek()
        }

        videoStart = findViewById<View>(R.id.video_start) as Button
        videoStop = findViewById<View>(R.id.video_stop) as Button
        videoRest = findViewById<View>(R.id.video_rest) as Button
        videoStart!!.setOnClickListener(this)
        videoStop!!.setOnClickListener(this)
        videoRest!!.setOnClickListener(this)
    }

    private var videoDecoder: VideoDecoder? = null
    private var audioDecoder: AudioDecoder? = null
    private fun initPlayer() {
        val path =
            Environment.getExternalStorageDirectory().absolutePath + "/lake.mp4"
        //        File file = new File(path);
//        if (!file.exists()) {
////            file.mkdir();
//        }
        //创建线程池
        val threadPool =
            Executors.newFixedThreadPool(2)

        //创建视频解码器
        videoDecoder = VideoDecoder(path, sfv, null)
        threadPool.execute(videoDecoder)
        //创建音频解码器
        audioDecoder = AudioDecoder(path)
        threadPool.execute(audioDecoder)
        audioDecoder!!.setStateListener(this)
        videoDecoder!!.setStateListener(this)

//
//        MP4Repack repack = new MP4Repack(path);
//        repack.start();
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.video_start) {
            //开启播放
            videoDecoder!!.goOn()
            audioDecoder!!.goOn()
        } else if (id == R.id.video_stop) {
            videoDecoder!!.stop()
            audioDecoder!!.stop()
        } else if (id == R.id.video_rest) {

        }
    }

    override fun onPause() {
        super.onPause()
        videoDecoder!!.release()
        audioDecoder!!.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoDecoder!!.release()
        audioDecoder!!.release()
    }

    override fun decoderPrepare(decodeJob: BaseDecoder) {}
    override fun decoderReady(decodeJob: BaseDecoder) {}
    override fun decoderRunning(decodeJob: BaseDecoder) {}
    override fun decoderPause(decodeJob: BaseDecoder) {}
    override fun decodeOneFrame(
        decodeJob: BaseDecoder,
        frame: Frame
    ) {
    }

    override fun decoderFinish(decodeJob: BaseDecoder) {}
    override fun decoderDestroy(decodeJob: BaseDecoder) {}
    override fun decoderError(decodeJob: BaseDecoder, msg: String) {}

    companion object {
        const val TAG = "VideoActivity"
    }
}