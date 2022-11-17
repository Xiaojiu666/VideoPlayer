package com.sn.videoplayer.data

import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File

object Config {

    var APP_PATH = ""

    fun setAppPath(rootPath: String) {
        APP_PATH = rootPath
    }

    var FOLDER_PATH_VIDEO = "/Video"
        get() {
            val file = File(APP_PATH + field)
            if (!file.exists()) {
                file.mkdirs()
            }
            return APP_PATH + field
        }


    var FOLDER_PATH_IMAGE = "/Image"
        get(){
            val file = File(APP_PATH + field)
            if (!file.exists()) {
                file.mkdirs()
            }
            return APP_PATH + field
        }


    var FILE_NAME_LAKE = "/lake.mp4"
        get() {
            val file = File(FOLDER_PATH_VIDEO + field)
            if (!file.exists()) {
                file.createNewFile()
            }
            return FOLDER_PATH_VIDEO + field
        }


}