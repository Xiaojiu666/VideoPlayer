package com.sn.videoplayer

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postAtTime

class MainActivity : AppCompatActivity() {

    var handler = MyHandler();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        Handler().post {
//            Log.d("MainActivity " ," post"+Thread.currentThread().name)
//        }
        Thread(Runnable {
            Log.e("Thread " , " Thread"+Thread.currentThread().name)

            handler.post {
                Log.e("Thread " , " Thread"+Thread.currentThread().name)
            }
        }).start()

    }


    class MyHandler:Handler(){
        override fun handleMessage(msg: Message) {
            Log.e("MainActivity " , "Msg ${msg.obj} ")
        }
    }

}