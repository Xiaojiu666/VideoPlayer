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


char *Media::getMediaInfo() {
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

void Media::openCodec() {
    AVCodecParameters *pCodecParam = pVStream->codecpar;
    AVCodec *pCodec = avcodec_find_decoder(pCodecParam->codec_id);
    if (!pCodec) {
        fprintf(stderr, "Codec not found\n");
        exit(1);
    }
    fprintf(stderr, "codecName = %s\n", pCodec->long_name);
    // 打开视频编码器
    //https://blog.csdn.net/qq_44857505/article/details/127305880
    pCodecCtx = avcodec_alloc_context3(
            pCodec); // avcodec_alloc_context3的作用是分配一个AVCodecContext并设置默认值
    if (avcodec_parameters_to_context(pCodecCtx, pCodecParam) < 0) {
        printf("Couldn't copy codec context.\r\n");
    }
    if (avcodec_open2(pCodecCtx, pCodec, NULL) < 0) {
        printf("Couldn't open codec.\r\n");
    }
}

void Media::generatePng(const char *filePath, Callback *generatePngCallback) {
    if (pCodecCtx == nullptr) {
        openCodec();
    }
    while (av_read_frame(ac, &packet) >= 0) {
        if (packet.stream_index == videoIndex) {
            avcodec_send_packet(pCodecCtx, &packet);
            frameFinished = avcodec_receive_frame(pCodecCtx, pFrame);
            // 保存packet
            if (frameFinished == 0) {
                LOGE(TAG, "pFrame %u ", pFrame->pict_type)
                if (pFrame->pict_type == AV_PICTURE_TYPE_I) {
                    //https://www.jianshu.com/p/cbe9abe89326
                    char filename[1024];
                    //把yuv数据保存为png图片
                    std::string const &cc = std::string(filePath) + std::string("/frame%d.png");
                    char const *imagePath = cc.c_str();
                    sprintf(filename,
                            imagePath,
                            i);
                    FILE *fp = fopen(filename, "wb");
                    if (fp == NULL) {
                        LOGE(TAG, "pen file error\r\n")
                    }
                    png_structp png_ptr = png_create_write_struct(PNG_LIBPNG_VER_STRING, NULL, NULL,
                                                                  NULL);
                    if (png_ptr == NULL) {
                        LOGE(TAG, "png_create_write_struct error\r\n");
                    }
                    png_infop info_ptr = png_create_info_struct(png_ptr);
                    if (info_ptr == NULL) {
                        LOGE(TAG, "png_create_info_struct error\r\n");
                    }
                    if (setjmp(png_jmpbuf(png_ptr))) {
                        LOGE(TAG, "setjmp error\r\n");
                    }
                    png_init_io(png_ptr, fp);
                    png_set_IHDR(png_ptr, info_ptr, pCodecCtx->width, pCodecCtx->height, 8,
                                 PNG_COLOR_TYPE_RGB, PNG_INTERLACE_NONE, PNG_COMPRESSION_TYPE_BASE,
                                 PNG_FILTER_TYPE_BASE);
                    png_write_info(png_ptr, info_ptr);
                    // pFrame->data[0]是Y分量，pFrame->data[1]是U分量，pFrame->data[2]是V分量 转换为RGB
                    uint8_t *out_buffer = (uint8_t *) av_malloc(
                            av_image_get_buffer_size(AV_PIX_FMT_RGB24, pCodecCtx->width,
                                                     pCodecCtx->height, 1));
                    if (out_buffer == NULL) {
                        LOGE(TAG, "av_malloc error\r\n");
                    }
                    av_image_fill_arrays(pFrameRGB->data, pFrameRGB->linesize, out_buffer,
                                         AV_PIX_FMT_RGB24, pCodecCtx->width, pCodecCtx->height, 1);
                    struct SwsContext *img_convert_ctx = sws_getContext(pCodecCtx->width,
                                                                        pCodecCtx->height,
                                                                        pCodecCtx->pix_fmt,
                                                                        pCodecCtx->width,
                                                                        pCodecCtx->height,
                                                                        AV_PIX_FMT_RGB24,
                                                                        SWS_BICUBIC,
                                                                        NULL, NULL, NULL);
                    if (img_convert_ctx == NULL) {
                        LOGE(TAG, "sws_getContext error\r\n");
                    }
                    sws_scale(img_convert_ctx, (const uint8_t *const *) pFrame->data,
                              pFrame->linesize,
                              0, pCodecCtx->height, pFrameRGB->data, pFrameRGB->linesize);
                    sws_freeContext(img_convert_ctx);
                    //把yuv数据保存为png图片
                    png_bytep row_pointers[pCodecCtx->height]; // 每一行的指针 数组 用于写入 png 图片
                    for (int i = 0; i < pCodecCtx->height; i++) {
                        row_pointers[i] = pFrameRGB->data[0] + i * pFrameRGB->linesize[0]; //
                    }

                    // yuv有三个通道，分别是Y,U,V
                    // Y通道的数据是连续的，U和V通道的数据是交错的
                    // YUV420P的数据格式是YYYYYYYYUUVV
                    // 现在要把YUV420P的数据可以直接存入png图片么
                    // for (int i = 0; i < pCodecCtx->height; i++)
                    // {
                    //     row_pointers[i] = pFrame->data[0] + i * pFrame->linesize[0];
                    // }
                    png_write_image(png_ptr, row_pointers);
                    png_write_end(png_ptr, NULL);
                    png_destroy_write_struct(&png_ptr, &info_ptr);
                    LOGE(TAG, "save frame%d.png\r\n", i);
                    fclose(fp);
                    // 保存yuv数据到图片文件
                    // sprintf(filename, "../video_resources/frame%d.yuv", i);
                    // FILE *pFile = fopen(filename, "wb");
                    // if (pFile == NULL)
                    // {
                    //     printf("Couldn't open file.\r \n");
                    //     return -1;
                    // }
                    // // 打印图片宽高
                    // printf("width: %d, height: %d\r\n", pFrame->width, pFrame->height);
                    // fwrite(pFrame->data[0], 1, pFrame->linesize[0] * pCodecCtx->height, pFile);
                    // fwrite(pFrame->data[1], 1, pFrame->linesize[1] * pCodecCtx->height / 2, pFile);
                    // fwrite(pFrame->data[2], 1, pFrame->linesize[2] * pCodecCtx->height / 2, pFile);
                    // fclose(pFile);
                    LOGE(TAG, "frame%d\r\n", filename);
                    generatePngCallback->callbackS("generatePngCallback", filename);
                }
                i++;
            }

            // if (frameFinished == 0) //
            // {
            //     i++;
            //     if (i > 100)
            //     {
            //         break;
            //     }
            // }
        }
        av_packet_unref(&packet);
    }

}

Media::~Media() {

}