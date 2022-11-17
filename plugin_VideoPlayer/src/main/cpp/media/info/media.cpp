//
// Created by edz on 2022/11/17.
//

#include "media.h"
#include "../../utils/logger.h"

static const char *TAG = "Media C++";

Media::Media(const char *filePath) {
    LOGE(TAG, "filePath %s", filePath)
    //https://www.php.cn/csharp-article-414735.html
    openCode = avformat_open_input(&ac, filePath, NULL, NULL);
    LOGE(TAG, "openCode %d", openCode)
    if (openCode < 0) {
        return;
    }
    LOGE(TAG, "nb_streams %u", ac->nb_streams)

    for (int i = 0; i < ac->nb_streams; i++) {
        if (ac->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            videoIndex = i;
            break;
        }
    }
    LOGD("Media C++ ", "videoIndex %d", videoIndex)
    if (videoIndex == -1) {
        return;
    }

    pVStream = ac->streams[videoIndex];

    char *string = getMediaInfo();
    LOGE("Media C++ ", "%s", string)
}


char* Media::getMediaInfo() {
    std::string videoInfo = "";
    if (openCode < 0) {
        videoInfo.append("open video error \n");
    } else {
        videoInfo.append("open video success  \n");
    }

    if (videoIndex == -1) {
        videoInfo.append("Didn't find a video stream  \n");
    }

    //https://blog.csdn.net/qq_41824928/article/details/103631719
    videoInfo += "duration = " + std::to_string(ac->duration / 1000000) + "s\n";
    videoInfo += "bitrate = " + std::to_string(ac->bit_rate / 1000) + "kb/s\n";
    videoInfo += "totalFrames = " + std::to_string(pVStream->nb_frames) + " 帧\n";
    videoInfo += "width  = " + std::to_string(pVStream->codecpar->width) + "，height  = " +
                 std::to_string(pVStream->codecpar->height) + "  \n";

//    LOGE(TAG, "videoInfo %s", videoInfo.c_str())
    char *string = const_cast<char *>(videoInfo.c_str());
    return string;
}

Media::~Media() {

}