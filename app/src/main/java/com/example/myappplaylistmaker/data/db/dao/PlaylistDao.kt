package com.example.myappplaylistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.myappplaylistmaker.data.db.cross_ref.PlaylistWithTracks
import com.example.myappplaylistmaker.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity): Int

//    @Query("SELECT * FROM playlist_table ORDER BY timestamp DESC")
//    fun getPlaylist(): Flow<List<PlaylistEntity>>

//    @Transaction
//    @Query("SELECT * FROM playlist_table ORDER BY timestamp DESC")
//    fun getPlaylistsWithTracksCount(): Flow<List<PlaylistWithTracks>>

    @Transaction
    @Query("SELECT * FROM playlist_table ORDER BY timestamp DESC")
    fun getPlaylistWithTracks(): Flow<List<PlaylistWithTracks>>
}
