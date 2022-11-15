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

jstring StringToJString(JNIEnv *env, const std::string &nativeString) {
    return env->NewStringUTF(nativeString.c_str());
}


JNIEXPORT jstring JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_videoInfo(JNIEnv *env, jobject thiz,
                                                                  jstring path) {
    AVFormatContext *ac = NULL;
    std::string videoInfo = "";
    const char *filepaht = env->GetStringUTFChars(path, NULL);
    int ret = avformat_open_input(&ac, filepaht, NULL, NULL);
    //https://www.php.cn/csharp-article-414735.html
    if (ret < 0) {
        videoInfo.append("open video error \n");
    } else {
        videoInfo.append("open video success  \n");
    }
    int videoindex = -1;
    for (int i = 0; i < ac->nb_streams; i++) {
        if (ac->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            videoindex = i;
            break;
        }
    }
    videoInfo += "streams size  = " + std::to_string(videoindex) + " \n";

    if (videoindex == -1) {
        printf("Didn't find a video stream.\n");
    }


    AVStream * pVStream = ac->streams[videoindex];

    //https://blog.csdn.net/qq_41824928/article/details/103631719
    videoInfo += "duration = " + std::to_string(ac->duration / 1000000) + "s\n";
    videoInfo += "bitrate = " + std::to_string(ac->bit_rate / 1000) + "kb/s\n";
    videoInfo += "totalFrames = " + std::to_string(pVStream-> nb_frames) + " 帧\n";
    videoInfo += "width  = " + std::to_string( pVStream->codecpar->width) +  "，height  = " + std::to_string( pVStream->codecpar->height) +"  \n";
    LOGE(TAG, "videoInfo %s", videoInfo.c_str())
    av_dump_format(ac, 0, filepaht, 0);
    avformat_close_input(&ac);
    return env->NewStringUTF(videoInfo.c_str());
}


}

