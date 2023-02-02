package com.sn.videoplayer.data

import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File

object Config {

    val TAG = "Config"
    var APP_PATH = ""

    val FILE_NAME_VOICE = "voice.mp4"

    val FILE_NAME_TRAILER = "trailer.mp4"

    var FILE_NAME_LAKE = "lake.mp4"


    // 默认视频文件
    var PLAY_FILE = FILE_NAME_TRAILER
    var PLAY_FILE_PATH = ""

    fun setAppPath(rootPath: String) {
        APP_PATH = rootPath
        PLAY_FILE_PATH = getFilePath4name(FOLDER_PATH_VIDEO, PLAY_FILE)
        Log.d(TAG, "setAppPath $PLAY_FILE_PATH")
    }


    val FOLDER_PATH_VIDEO = "/Video"
        get() {
            val file = File(APP_PATH + field)
            if (!file.exists()) {
                file.mkdirs()
            }
            Log.d(TAG, "FOLDER_PATH_VIDEO ${APP_PATH + field}")
            return APP_PATH + field
        }


    var FOLDER_PATH_IMAGE = "/Image"
        get() {
            val file = File(APP_PATH + field)
            if (!file.exists()) {
                file.mkdirs()
            }
            return APP_PATH + field
        }


    private fun getFilePath4name(fileFolder: String, fileName: String): String {
        val file = File("$fileFolder/$fileName")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file.path
    }


}