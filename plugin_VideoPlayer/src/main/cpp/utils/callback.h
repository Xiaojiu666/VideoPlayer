//
// Created by edz on 2022/11/18.
//

#ifndef VIDEOPLAYER_CALLBACK_H
#define VIDEOPLAYER_CALLBACK_H


#include <jni.h>
#include <string>

class Callback {
public:

    Callback(JNIEnv *env, jobject obj);

    ~Callback();

    void callbackI(const char* name,int value);

    void callbackD(const char* name,double value);

    void callbackS(const char* name,const char *value);

    void callbackMediaInfo(const char *methodName,int value);

    void callbackS(const char* name,const char *TAG,const char *value);

private:

    JNIEnv *jniEnv= NULL;

    jobject jobJect= NULL;;

    jmethodID jMethodId= NULL;



};


#endif //VIDEOPLAYER_CALLBACK_H
