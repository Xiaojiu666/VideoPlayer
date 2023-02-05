package com.sn.videoplayer.utils

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.sn.videoplayer.VideoActivity
import com.sn.videoplayer.data.Config
import com.sn.videoplayer.worker.CopyFileWork

fun  copyFile(owner: LifecycleOwner, context: Context){
    val request = OneTimeWorkRequestBuilder<CopyFileWork>()
        .setInputData(workDataOf(CopyFileWork.KEY_FILEPATH to Config.PLAY_FILE_PATH))
        .build()
    WorkManager.getInstance(context).enqueue(request)
    WorkManager.getInstance(context).getWorkInfoByIdLiveData(request.id)
        .observe(owner, Observer<WorkInfo> {
            Log.d(VideoActivity.TAG, "state " + it.state)
            if (it.state == WorkInfo.State.FAILED) {
                val outputData = it.outputData.getString("out_put")
                Log.d(VideoActivity.TAG, "outputData $outputData")
            }
        })
}