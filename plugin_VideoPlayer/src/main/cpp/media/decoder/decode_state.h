//
// Created by edz on 2021/4/9.
//

#ifndef VIDEOPLAYER_DECODE_STATE_H
#define VIDEOPLAYER_DECODE_STATE_H

enum DecodeState {
    STOP,
    PREPARE,
    START,
    DECODING,
    PAUSE,
    FINISH
};
#endif //VIDEOPLAYER_DECODE_STATE_H
