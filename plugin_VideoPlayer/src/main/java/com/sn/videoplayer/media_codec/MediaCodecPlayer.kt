package com.sn.videoplayer.media_codec

import android.view.Surface
import android.view.SurfaceView
import com.sn.videoplayer.PlayerControl
import com.sn.videoplayer.media_codec.decoder.AudioDecoder
import com.sn.videoplayer.media_codec.decoder.BaseDecoder
import com.sn.videoplayer.media_codec.decoder.IDecoderStateListener
import com.sn.videoplayer.media_codec.decoder.VideoDecoder
import java.util.concurrent.Executors

class MediaCodecPlayer(var sfv: SurfaceView, var filePath: String) : PlayerControl(),
    IDecoderStateListener {

    var videoDecoder: VideoDecoder? = null
    var audioDecoder: AudioDecoder? = null

    init {
        //创建线程池
        val threadPool =
            Executors.newFixedThreadPool(2)

        //创建视频解码器
        videoDecoder = VideoDecoder(filePath, sfv, null)
        threadPool.execute(videoDecoder)
        //创建音频解码器
        audioDecoder = AudioDecoder(filePath)
        threadPool.execute(audioDecoder)
        audioDecoder!!.setStateListener(this)
        videoDecoder!!.setStateListener(this)
    }

    override fun initVideoPlayer(surface: Surface, filePath: String): Int {
        return 0;
    }

    override fun start() {
        //开启播放
        videoDecoder!!.goOn()
        audioDecoder!!.goOn()
    }

    override fun stop() {
        videoDecoder!!.stop()
        audioDecoder!!.stop()
    }


    override fun onDestroy() {
        videoDecoder!!.release()
        audioDecoder!!.release()
    }

    override fun decoderReady(decodeJob: BaseDecoder?) {
    }

    override fun decoderDestroy(decodeJob: BaseDecoder?) {
    }

    override fun decoderPause(decodeJob: BaseDecoder?) {
    }

    override fun decoderPrepare(decodeJob: BaseDecoder?) {
    }

    override fun decoderRunning(decodeJob: BaseDecoder?) {
    }

    override fun decoderError(decodeJob: BaseDecoder?, msg: String?) {
    }

    override fun decoderFinish(decodeJob: BaseDecoder?) {
    }

    override fun decodeOneFrame(decodeJob: BaseDecoder?, frame: Frame?) {
    }
}