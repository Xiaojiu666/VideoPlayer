cmake_minimum_required(VERSION 3.4.1)

# 支持gnu++11
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

# 1. 定义so库和头文件所在目录，方面后面使用
set(ffmpeg_lib_dir ${CMAKE_SOURCE_DIR}/../libs/${ANDROID_ABI})
#

#set(distribution_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../app)
#set_target_properties(XXXX PROPERTIES
#        LIBRARY_OUTPUT_DIRECTORY
#        ${distribution_DIR}/libs/${ANDROID_ABI})

#set(ffmpeg_lib_dir  /Users/edz/StudioProjects/VideoPlayer/plugin_VideoPlayer/src/libs/${ANDROID_ABI})
#set(ffmpeg_lib_dir  ${PROJECT_BINARY_DIR}/libs/${ANDROID_ABI})
set(ffmpeg_head_dir ${CMAKE_SOURCE_DIR}/ffmpeg)

# 2. 添加头文件目录
include_directories(${ffmpeg_head_dir}/include)

# 配置 repidjson 库
#set(HEADER_RAPIDJSON_INC ${CMAKE_SOURCE_DIR}/repidjson/include)
#include_directories(${HEADER_RAPIDJSON_INC})  # 增加include
#
#include_directories(jsoncpp)
#add_subdirectory(jsoncpp)

#add_library(jsoncpp
#        SHARED
#        ${CMAKE_SOURCE_DIR}/jsoncpp)
#set_target_properties( jsoncpp
#        PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/jsoncpp )

add_library(image
        SHARED
        IMPORTED)
set_target_properties(image
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libpng.so)


# 3. 添加ffmpeg相关的so库
add_library(avutil
        SHARED
        IMPORTED)
set_target_properties(avutil
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavutil.so)

add_library(swresample
        SHARED
        IMPORTED)
set_target_properties(swresample
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libswresample.so)

add_library(avcodec
        SHARED
        IMPORTED)
set_target_properties(avcodec
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavcodec.so)

add_library(avfilter
        SHARED
        IMPORTED)
set_target_properties(avfilter
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavfilter.so)

add_library(swscale
        SHARED
        IMPORTED)
set_target_properties(swscale
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libswscale.so)

add_library(avformat
        SHARED
        IMPORTED)
set_target_properties(avformat
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavformat.so)

add_library(avdevice
        SHARED
        IMPORTED)
set_target_properties(avdevice
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavdevice.so)

# 查找代码中使用到的系统库
find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)
add_library(h264
        SHARED
        IMPORTED)
set_target_properties(h264
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libh264.so)
#找打Android lib库里面的libOpenSLES.so的库
find_library(OpenSLES-lib
        OpenSLES)

# 配置目标so库编译信息
add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp

        # 工具
        ${CMAKE_SOURCE_DIR}/utils/logger.h
        ${CMAKE_SOURCE_DIR}/utils/timer.c
        ${CMAKE_SOURCE_DIR}/utils/callback.h
        ${CMAKE_SOURCE_DIR}/utils/callback.cpp
        ${CMAKE_SOURCE_DIR}/media/const.h
        #
        #        # 解码器
        ${CMAKE_SOURCE_DIR}/media/one_frame.h
        ${CMAKE_SOURCE_DIR}/media/decoder/i_decoder.h
        ${CMAKE_SOURCE_DIR}/media/decoder/decode_state.h
        ${CMAKE_SOURCE_DIR}/media/decoder/i_decode_state_cb.cpp
        ${CMAKE_SOURCE_DIR}/media/decoder/base_decoder.cpp
        ${CMAKE_SOURCE_DIR}/media/decoder/video/v_decoder.cpp
        ${CMAKE_SOURCE_DIR}/media/decoder/audio/a_decoder.cpp

        # 播放器
        ${CMAKE_SOURCE_DIR}/media/player/def_player/player.cpp
        #        ${CMAKE_SOURCE_DIR}/media/player/gl_player/gl_player.cpp

        # 渲染器
        # 必须以来.h 在依赖CPP
        ${CMAKE_SOURCE_DIR}/media/render/video/video_render.h
        ${CMAKE_SOURCE_DIR}/media/render/video/native_render/native_render.cpp
        ${CMAKE_SOURCE_DIR}/media/render/audio/audio_render.h
        ${CMAKE_SOURCE_DIR}/media/render/audio/opensl_render.cpp

        ${CMAKE_SOURCE_DIR}/media/info/media.h
        ${CMAKE_SOURCE_DIR}/media/info/media.cpp

        ${CMAKE_SOURCE_DIR}/media/info/media.h
        ${CMAKE_SOURCE_DIR}/media/info/media.cpp

        #jsoncpp 引用 https://blog.csdn.net/lftaoyuan/article/details/107057766
        jsoncpp/json_tool.h
        jsoncpp/json_reader.cpp
        jsoncpp/json_valueiterator.inl
        jsoncpp/json_value.cpp
        jsoncpp/json_writer.cpp
        jsoncpp/assertions.h
        jsoncpp/allocator.h
        jsoncpp/config.h
        jsoncpp/json_features.h
        jsoncpp/forwards.h
        jsoncpp/json.h
        jsoncpp/reader.h
        jsoncpp/value.h
        jsoncpp/version.h
        jsoncpp/writer.h
        )

# 指定编译目标库时，cmake要链接的库
target_link_libraries(


        # 指定目标库，native-lib 是在上面 add_library 中配置的目标库
        native-lib

        # 4. 连接 FFmpeg 相关的库
        avutil
        swresample
        avcodec
        avfilter
        swscale
        avformat
        avdevice
        h264
        -landroid
        #        ESOpenSL
        #        -lEGL
        #        -lGLESv2
        # Links the target library to the log library
        # included in the NDK.
        ${OpenSLES-lib}
        ${log-lib}
        image
#        jsoncpp
)