package com.example.myappplaylistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.example.myappplaylistmaker.data.db.cross_ref.TrackWithPlaylists
import com.example.myappplaylistmaker.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Upsert
    suspend fun insertTrackToFavs(track: TrackEntity)

    @Update
    suspend fun updateTrack(track: TrackEntity) : Int

    @Query("SELECT * FROM track_table WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM track_table WHERE isFavorite = 1")
    suspend fun getTrackId(): List<String>

    @Transaction
    @Query("SELECT * FROM track_table WHERE trackId = :trackId")
    fun getTrackWithPlaylists(trackId: String): Flow<TrackWithPlaylists>

    @Query("""
    SELECT EXISTS(
        SELECT 1
        FROM track_table
        WHERE trackId = :trackId AND isFavorite = 1
    )
""")
    suspend fun isTrackInFavorites(trackId: String): Boolean

    @Query("DELETE FROM track_table WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: String)
}
