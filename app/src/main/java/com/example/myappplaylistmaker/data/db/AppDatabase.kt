package com.example.myappplaylistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myappplaylistmaker.data.db.dao.PlaylistDao
import com.example.myappplaylistmaker.data.db.dao.PlaylistTrackCrossRefDao
import com.example.myappplaylistmaker.data.db.dao.TrackDao
import com.example.myappplaylistmaker.data.db.entity.PlaylistEntity
import com.example.myappplaylistmaker.data.db.entity.PlaylistTrackCrossRef
import com.example.myappplaylistmaker.data.db.entity.TrackEntity

@Database(version = 8, entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackCrossRef::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackCrossRefDao(): PlaylistTrackCrossRefDao
}