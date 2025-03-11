package com.example.myappplaylistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.myappplaylistmaker.data.db.cross_ref.TrackWithPlaylists
import com.example.myappplaylistmaker.data.db.entity.PlaylistEntity
import com.example.myappplaylistmaker.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity) : Int

    @Query("SELECT * FROM track_table WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM track_table WHERE isFavorite = 1")
    suspend fun getTrackId(): List<String>

    @Transaction
    @Query("SELECT * FROM track_table WHERE trackId = :trackId")
    fun getTrackWithPlaylists(trackId: String): Flow<TrackWithPlaylists>

}
