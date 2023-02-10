package com.sn.videoplayer.ffmpeg.demo

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class MediaInfo() : Parcelable {
    var duration = 0
    var bitRate = 0
    var frames = 0
    var height = 0
    var width = 0

    constructor(parcel: Parcel) : this() {
        duration = parcel.readInt()
        bitRate = parcel.readInt()
        frames = parcel.readInt()
        height = parcel.readInt()
        width = parcel.readInt()
    }

    override fun toString(): String {
        return "MediaInfo{" +
                "duration=" + duration +
                ", bitRate=" + bitRate +
                ", frames=" + frames +
                ", height=" + height +
                ", width=" + width +
                '}'
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(duration)
        parcel.writeInt(bitRate)
        parcel.writeInt(frames)
        parcel.writeInt(height)
        parcel.writeInt(width)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MediaInfo> {
        override fun createFromParcel(parcel: Parcel): MediaInfo {
            return MediaInfo(parcel)
        }

        override fun newArray(size: Int): Array<MediaInfo?> {
            return arrayOfNulls(size)
        }
    }
}