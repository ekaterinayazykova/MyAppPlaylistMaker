package com.example.myappplaylistmaker.domain.entity

import android.os.Parcel
import android.os.Parcelable

data class Track (
        val trackName: String? = null,
        val artistName: String? = null,
        val artworkUrl100: String,
        val trackTimeMillis: Long =0L,
        val collectionName: String? = null,
        val releaseDate: String? = null,
        val country: String? = null,
        val previewUrl: String

    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readLong(),
            parcel.readString() ?: "",
            parcel.readString().toString(),
            parcel.readString() ?: "",
            parcel.readString() ?: ""
        )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(trackName)
        parcel.writeString(artistName)
        parcel.writeString(artworkUrl100)
        parcel.writeLong(trackTimeMillis)
        parcel.writeString(collectionName)
        parcel.writeString(releaseDate)
        parcel.writeString(country)
        parcel.writeString(previewUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }
}