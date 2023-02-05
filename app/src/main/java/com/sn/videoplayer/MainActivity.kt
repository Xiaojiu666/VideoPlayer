package com.sn.videoplayer

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sn.videoplayer.data.GuideTitle
import com.sn.videoplayer.ffmpeg.demo.FFmpegPlayer
import com.sn.videoplayer.view.GuideAdapter
import getPath
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*

class MainActivity : AppCompatActivity(), GuideAdapter.OnClickListener {
    var TAG = "MainActivity"
    var titles = arrayOf("音视频编解码-软解码", "音视频编解码-硬解码")
    var descs = arrayOf("FFmpeg", "MediaCodec")
    var data = ArrayList<GuideTitle>()
    var storagePermission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    var videoFilePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        initView()
        initRecyclerView()
    }

    private fun initView() {
        to_file_selector.setOnClickListener {
            requestStoragePermission()
        }
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
    }

    override fun onClick(view: View, position: Int) {
        when (position) {
            0 -> {
                toSoftActivity()
            }
//            1 -> {
//                toHardActivity()
//            }
        }
    }
    
    private fun toSoftActivity() {
        if(TextUtils.isEmpty(videoFilePath)){
            Toast.makeText(baseContext,getString(R.string.tip_selector_file),Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, VideoActivity::class.java)
        intent.putExtra("videoFilePath",videoFilePath)
        startActivity(intent)
    }

    private fun toHardActivity() {
        val intent = Intent(this, VideoHardActivity::class.java)
        intent.putExtra("videoFilePath",videoFilePath)
        startActivity(intent)
    }


    private fun requestStoragePermission() {
        result4StoragePermission.launch(storagePermission)
    }


    private val registerForActivityResult = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
        if (it == null){
            return@registerForActivityResult
        }
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat){
            videoFilePath = getPath(this, it)!!
            tv_file_name.text = videoFilePath
        }
    }

    private val result4StoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it[Manifest.permission.READ_EXTERNAL_STORAGE]!!&&it[Manifest.permission.WRITE_EXTERNAL_STORAGE]!!){
            registerForActivityResult.launch(arrayOf("*/*"))
        }else{
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

}