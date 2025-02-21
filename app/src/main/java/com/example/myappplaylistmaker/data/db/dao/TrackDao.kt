package com.example.myappplaylistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myappplaylistmaker.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity) : Int

    @Query("SELECT * FROM track_table")
    suspend fun getFavTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM track_table")
    suspend fun getTrackId(): List<String>
}