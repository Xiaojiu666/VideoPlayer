//
// Created by edz on 2021/4/6.
//
#include <jni.h>
#include <string>
#include <unistd.h>
#include "media/player/def_player/player.h"
#include "utils/logger.h"
const char *TAG = "AVCodec info";

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>
#include <libavcodec/jni.h>
#include "../../utils/logger.h"
int volatile gIsThreadStop = 0;

    JNIEXPORT jstring JNICALL
    Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_ffmpegInfo(JNIEnv *env,jobject obj /* this */) {
        char info[40000] = {0};
        AVCodec *c_temp = av_codec_next(NULL);
        while (c_temp != NULL) {
            if (c_temp->decode != NULL) {
                //数据写入某个字符串中
//                sprintf(info, "decode: %s", info);
//                LOGE(TAG, "decode before info %s",info)
                switch (c_temp->type) {
                    case AVMEDIA_TYPE_VIDEO:
                        sprintf(info, "%s(video):", info);
//                        LOGE(TAG, "%s(video):", info)
                        break;
                    case AVMEDIA_TYPE_AUDIO:
                        sprintf(info, "%s(audio):", info);
                        break;
                    default:
                        sprintf(info, "%s(other):", info);
                        break;
                }
                LOGE(TAG, "decode after %s",info)
                sprintf(info, "%s[%10s]\n", info, c_temp->name);
                LOGE(TAG, "c_temp->name %s", c_temp->name)
            } else {
//                sprintf(info, "%sencode:", info);
            }
            c_temp = c_temp->next; // ->等于取值
        }

        return env->NewStringUTF(info);
    }

    JNIEXPORT jint JNICALL
    Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_createPlayer(JNIEnv *env,
                                                           jobject obj  /* this */,
                                                           jstring path,
                                                           jobject surface) {


        Player *player = new Player(env,obj, path, surface);
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
    Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_threadStart(JNIEnv *env, jobject obj  /* this */){

        jobject gJavaObj = env->NewGlobalRef(obj);
        jclass thiz = env->GetObjectClass(gJavaObj);
        jmethodID nativeCallback = env->GetMethodID(thiz,"nativeCallback","(I)V");
        if (nativeCallback == NULL){
            LOGE(TAG, "nativeCallback is null")
        }
        env->CallVoidMethod(obj,nativeCallback,100);
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


}
