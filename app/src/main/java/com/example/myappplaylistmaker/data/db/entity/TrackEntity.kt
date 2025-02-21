package com.example.myappplaylistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
data class TrackEntity(
    @PrimaryKey(autoGenerate = false)
    val trackId: String,
    val trackName: String? = null,
    val artistName: String? = null,
    val artworkUrl: String,
    val trackTimeMillis: Long =0L,
    val collectionName: String?,
    val releaseDate: String?,
    val genre: String?,
    val country: String?,
    val previewUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)