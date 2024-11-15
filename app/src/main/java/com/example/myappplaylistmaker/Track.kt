package com.example.myappplaylistmaker

import android.os.Parcel
import android.os.Parcelable

data class Track (
        val trackName: String,
        val artistName: String,
        val artworkUrl100: String,
        val trackTimeMillis: Long,
        val collectionName: String,
        val releaseDate: String,
        val country: String
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readLong(),
            parcel.readString() ?: "",
            parcel.readString().toString(),
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