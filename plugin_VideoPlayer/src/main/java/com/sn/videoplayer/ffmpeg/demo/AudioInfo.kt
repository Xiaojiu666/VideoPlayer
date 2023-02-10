package com.sn.videoplayer.ffmpeg.demo

import android.os.Parcel
import android.os.Parcelable

class AudioInfo() : Parcelable {
    var channels = 0
    var sample_rate = 0

    constructor(parcel: Parcel) : this() {
        channels = parcel.readInt()
        sample_rate = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(channels)
        parcel.writeInt(sample_rate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AudioInfo> {
        override fun createFromParcel(parcel: Parcel): AudioInfo {
            return AudioInfo(parcel)
        }

        override fun newArray(size: Int): Array<AudioInfo?> {
            return arrayOfNulls(size)
        }
    }
}