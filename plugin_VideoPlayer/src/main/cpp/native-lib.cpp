//
// Created by edz on 2021/4/6.
//
#include <jni.h>
#include <string>
#include <unistd.h>
#include <random>
#include <__locale>
#include "media/player/def_player/player.h"
#include "utils/logger.h"

const char *TAG = "AVCodec info";

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>
#include <libavcodec/jni.h>
int volatile gIsThreadStop = 0;

JNIEXPORT jstring JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_ffmpegInfo(JNIEnv *env,
                                                                   jobject /* this */) {

    char info[40000] = {0};
    AVCodec *c_temp = av_codec_next(NULL);
//    while (c_temp != NULL) {
//        if (c_temp->decode != NULL) {
//            //数据写入某个字符串中
////                sprintf(info, "decode: %s", info);
////                LOGE(TAG, "decode before info %s",info)
//            switch (c_temp->type) {
//                case AVMEDIA_TYPE_VIDEO:
//                    sprintf(info, "%s(video):", info);
////                        LOGE(TAG, "%s(video):", info)
//                    break;
//                case AVMEDIA_TYPE_AUDIO:
//                    sprintf(info, "%s(audio):", info);
//                    break;
//                default:
//                    sprintf(info, "%s(other):", info);
//                    break;
//            }
//            LOGE(TAG, "decode after %s", info)
//            sprintf(info, "%s[%10s]\n", info, c_temp->name);
//            LOGE(TAG, "c_temp->name %s", c_temp->name)
//        } else {
////                sprintf(info, "%sencode:", info);
//        }
//        c_temp = c_temp->next; // ->等于取值
//    }

    return env->NewStringUTF(info);
}

JNIEXPORT jint JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_createPlayer(JNIEnv *env,
                                                                     jobject obj/* this */,
                                                                     jstring path,
                                                                     jobject surface) {
    Player *player = new Player(env, obj, path, surface);
    return (jint) player;
}

JNIEXPORT void JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_play(JNIEnv *env,
                                                             jobject  /* this */,
                                                             jint player) {
    Player *p = (Player *) player;
    p->play();
}

JNIEXPORT void JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_pause(JNIEnv *env,
                                                              jobject  /* this */,
                                                              jint player) {
    Player *p = (Player *) player;
    p->pause();
}
//    https://www.cnblogs.com/seven-sky/p/7205932.html
JNIEXPORT void JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_threadStart(JNIEnv *env,
                                                                    jobject  /* this */) {

}

JNIEXPORT void JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_threadStop(JNIEnv *env,
                                                                   jobject  /* this */) {

}

JNIEXPORT jstring JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_videoTime(JNIEnv *env, jobject thiz,
                                                                  jint player) {
    Player *p = (Player *) player;
    return env->NewStringUTF(p->viedeotime());
}

JNIEXPORT jint JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_videoTotalTime(JNIEnv *env, jobject thiz,
                                                                       jint player) {
    Player *p = (Player *) player;
    return (jint) p->videoTimeTotal();
}

JNIEXPORT jstring JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_videoInfo(JNIEnv *env, jobject thiz,
                                                                  jstring path) {
    AVFormatContext *ac = NULL;
//    char videoInfo[40000] = {0};
    std::string videoInfo="";


//    char *videoInfo;
    int ret = 0;
    const char *filepaht = env->GetStringUTFChars(path, NULL);
    ret = avformat_open_input(&ac, filepaht, NULL, NULL);
//    https://www.php.cn/csharp-article-414735.html
//    fprintf(stderr, "duration = %.2fs\n", (double)ac->duration / 1000000);
//    fprintf(stderr, "bitrate = %.2fkb/s\n", (double)ac->bit_rate / 1000);
//    sprintf(videoInfo, "open %s(video) error:", videoInfo);
    if (ret < 0) {
        videoInfo.append("open(fail)");
//        sprintf(videoInfo, "open(fail) : %s",videoInfo);
//        av_log(NULL, AV_LOG_ERROR, "open %s error. \n\n", fileName);
    } else {
        videoInfo.append("open(success)");
//        sprintf(videoInfo, "open (success) : %s",videoInfo);
//        av_log(NULL, AV_LOG_INFO, "open %s success. \n\n", (double)ac->duration / 1000000);
    }
    //https://blog.csdn.net/qq_41824928/article/details/103631719
    videoInfo.append( "duration = %.2fs\n", (double)ac->duration / 1000000);
    videoInfo.append(  "bitrate = %.2fkb/s\n", (double)ac->bit_rate  / 1000);

//    sprintf(videoInfo, "duration = %.2fs\n", (double)ac->duration / 1000000);
//    sprintf(videoInfo, "bitrate = %.2fkb/s\n", (double)ac->bit_rate  / 1000);
//    LOGE(TAG, "decode after %s", videoInfo)
    av_dump_format(ac, 0, filepaht, 0);
//    avformat_close_input(&ac);
    //close file flow
    avformat_close_input(&ac);
    return env->New(videoInfo);
}


}
