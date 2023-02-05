package com.sn.videoplayer.ffmpeg.demo;

public class MediaInfo {
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    int duration;
    int bitRate;
    int frames;
    int height;
    int width;

    @Override
    public String toString() {
        return "MediaInfo{" +
                "duration=" + duration +
                ", bitRate=" + bitRate +
                ", frames=" + frames +
                ", height=" + height +
                ", width=" + width +
                '}';
    }
}
