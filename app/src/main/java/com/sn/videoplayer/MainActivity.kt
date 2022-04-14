package com.sn.videoplayer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.sn.videoplayer.data.Config
import com.sn.videoplayer.data.GuideTitle
import com.sn.videoplayer.view.GuideAdapter
import com.sn.videoplayer.worker.CopyFileWork
import com.sn.videoplayer.worker.CopyFileWork.Companion.KEY_FILEPATH
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.logging.Logger

class MainActivity : AppCompatActivity(), GuideAdapter.OnClickListener {
    var TAG = "MainActivity"
    var titles = arrayOf("音视频编解码-软解码", "音视频编解码-硬解码")
    var descs = arrayOf("FFmpeg", "MediaCodec")
    var data = ArrayList<GuideTitle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        initRecyclerView()
    }

    private fun initData() {
        for (x in titles.indices) {
            data.add(GuideTitle(titles[x], descs[x]))
        }
    }

    private fun initRecyclerView() {
        guide_recyclerView.layoutManager = LinearLayoutManager(baseContext)
        val guideAdapter = GuideAdapter(data, baseContext)
        guide_recyclerView.adapter =
            guideAdapter
        guide_recyclerView.addItemDecoration(
            DividerItemDecoration(
                baseContext,
                LinearLayoutManager.VERTICAL
            )
        )
        guideAdapter.onClickListener = this
        val request = OneTimeWorkRequestBuilder<CopyFileWork>()
            .setInputData(workDataOf(KEY_FILEPATH to Config.FILE_PATH))
            .build()
        WorkManager.getInstance(baseContext).enqueue(request)
        WorkManager.getInstance(baseContext).getWorkInfoByIdLiveData(request.id)
            .observe(this, Observer<WorkInfo> {
                Log.d(TAG, "state " + it.state)
                if (it.state == WorkInfo.State.FAILED) {
                    val outputData = it.getOutputData().getString("out_put")
                    Log.d(TAG, "outputData $outputData")
                }
            })
    }

    override fun onClick(view: View, position: Int) {
        when (position) {
            0 -> {
                toSoftActivity()
            }
            1 -> {
                toHardActivity()
            }
        }
    }


    private fun toSoftActivity() {
        val intent = Intent(this, VideoActivity::class.java)
        startActivity(intent)
    }

    private fun toHardActivity() {
        val intent = Intent(this, VideoHardActivity::class.java)
        startActivity(intent)
    }

}