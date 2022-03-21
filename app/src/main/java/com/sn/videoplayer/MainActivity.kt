package com.sn.videoplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sn.videoplayer.data.GuideTitle
import com.sn.videoplayer.view.GuideAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), GuideAdapter.OnClickListener {

    var titles = arrayOf("音视频编解码")
    var descs = arrayOf("音视频编解码(包含软编码FFmpeg /硬编码MediaCodeC)")
    var data  = ArrayList<GuideTitle>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        initRecyclerView()
    }

    private fun initData() {
        for (x in titles.indices) {
            data.add(GuideTitle(titles[x],descs[x]))
        }
    }

    private fun initRecyclerView() {
        guide_recyclerView.layoutManager = LinearLayoutManager(baseContext)
        val guideAdapter = GuideAdapter(data, baseContext)
        guide_recyclerView.adapter =
            guideAdapter
        guide_recyclerView.addItemDecoration(DividerItemDecoration(baseContext, LinearLayoutManager.VERTICAL))
        guideAdapter.onClickListener = this
    }

    override fun onClick(view: View, position: Int) {
       when(position){
           0 -> {
               val intent = Intent(MainActivity@ this, VideoActivity::class.java)
               startActivity(intent)
           }
       }
    }

}