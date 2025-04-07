package com.example.myappplaylistmaker.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Playlist (
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescription: String? = null,
    val imagePath: String = "playlist_placeholder",
    var amountOfTracks: Int = 0,
    val timestamp: Long? = System.currentTimeMillis()
): Parcelable
