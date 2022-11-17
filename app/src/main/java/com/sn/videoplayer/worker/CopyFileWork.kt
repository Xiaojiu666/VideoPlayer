package com.sn.videoplayer.worker

import android.content.Context
import android.util.JsonReader
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream

class CopyFileWork(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "SeedDatabaseWorker"
        const val KEY_FILENAME = "PLANT_DATA_FILENAME"
        const val KEY_FILEPATH = "FILE_PATH"
    }

    override suspend fun doWork(): Result = coroutineScope {
            val filePath = inputData.getString(KEY_FILEPATH)
            if (filePath != null) {
                val file = File(filePath)
                Log.e(TAG, "filePath "+ file.exists())
                if (!file.exists()) {
                    val outPut = Data.Builder().putString("out_put", "File no exists").build()
                    Result.failure(outPut)
                }else{
                    Log.e(TAG, "filePath $filePath")
                    applicationContext.assets.open("lake.mp4").use { inputStream ->
                        val fos = FileOutputStream(file)
                        val bis = BufferedInputStream(inputStream)
                        val buffer = ByteArray(1024)
                        var len = 0
                        while (bis.read(buffer).also { len = it } != -1) {
                            fos.write(buffer, 0, len)
                        }
                        fos.close()
                        bis.close()
                        inputStream.close()
                        Result.success()
                    }
                }
            } else {
                Log.e(TAG, "Error seeding database - no valid filename")
                Result.failure()
            }
        }
}