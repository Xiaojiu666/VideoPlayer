package com.sn.videoplayer.ffmpeg.demo;

public class DemoNativeInterface {
    static {
        System.loadLibrary("native-lib");
    }

    public static native String stringFromJNI();


//    public static native String ffmpegInfo();
}
