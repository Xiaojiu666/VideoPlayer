//
// Created by edz on 2021/4/6.
//
#include <jni.h>
#include <string>
#include <unistd.h>
#include "media/player/def_player/player.h"

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>
#include <libavcodec/jni.h>
    JNIEXPORT jstring JNICALL
    Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_ffmpegInfo(JNIEnv *env,
                                                                       jobject /* this */) {
    //        std::string hello = "(*^__^*) 嘻嘻……~Hello from C++ 隔壁老李头";
    //        return env->NewStringUTF(hello.c_str());
        char info[40000] = {0};
        AVCodec *c_temp = av_codec_next(NULL);
        while (c_temp != NULL) {
            if (c_temp->decode != NULL) {
                sprintf(info, "%sdecode:", info);
                switch (c_temp->type) {
                    case AVMEDIA_TYPE_VIDEO:
                        sprintf(info, "%s(video):", info);
                        break;
                    case AVMEDIA_TYPE_AUDIO:
                        sprintf(info, "%s(audio):", info);
                        break;
                    default:
                        sprintf(info, "%s(other):", info);
                        break;
                }
                sprintf(info, "%s[%10s]\n", info, c_temp->name);
            } else {
                sprintf(info, "%sencode:", info);
            }
            c_temp = c_temp->next; // ->等于取值
        }

        return env->NewStringUTF(info);
    }

    JNIEXPORT jint JNICALL
    Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_createPlayer(JNIEnv *env,
                                                           jobject  /* this */,
                                                           jstring path,
                                                           jobject surface) {
        Player *player = new Player(env, path, surface);
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
}

