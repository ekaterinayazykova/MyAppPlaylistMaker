package com.example.myappplaylistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescription: String? = null,
    val imagePath: String = "playlist_placeholder",
    val amountOfTracks: Int? = null,
    val timestamp: Long? = System.currentTimeMillis()
)
