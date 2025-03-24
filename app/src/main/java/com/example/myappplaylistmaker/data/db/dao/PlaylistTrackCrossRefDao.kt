package com.example.myappplaylistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myappplaylistmaker.data.db.entity.PlaylistTrackCrossRef
import com.example.myappplaylistmaker.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: PlaylistTrackCrossRef)

    @Delete
    suspend fun deleteCrossRef(crossRef: PlaylistTrackCrossRef)

    @Query("SELECT COUNT(*) FROM playlist_track_cross_ref WHERE trackId = :trackId")
    suspend fun getTrackUsageCount(trackId: String): Int

    @Query("""
        SELECT t.*
        FROM track_table t
        INNER JOIN playlist_track_cross_ref c 
            ON t.trackId = c.trackId
        WHERE c.playlistId = :playlistId
        ORDER BY c.addedAt DESC
    """)
    fun getTracksForPlaylist(playlistId: Int): Flow<List<TrackEntity>>

    @Query("""
    SELECT EXISTS(
        SELECT 1
        FROM playlist_track_cross_ref
        WHERE playlistId = :playlistId AND trackId = :trackId
    )
""")
    suspend fun isTrackInPlaylist(playlistId: Int, trackId: String): Boolean

    @Query("SELECT * FROM playlist_track_cross_ref")
    suspend fun getAllCrossRefs(): List<PlaylistTrackCrossRef>

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun deleteAllCrossRefsForPlaylist(playlistId: Int)

}