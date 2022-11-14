package com.sn.videoplayer.data

import android.os.Environment

object Config {
    var FILE_PATH =
        Environment.getExternalStorageDirectory().absolutePath + "/lake.mp4"

    fun setFilePath(rootPath: String) {
        FILE_PATH=rootPath
    }

}