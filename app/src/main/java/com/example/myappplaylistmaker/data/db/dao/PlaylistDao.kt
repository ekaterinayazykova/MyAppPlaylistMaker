package com.example.myappplaylistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.myappplaylistmaker.data.db.cross_ref.PlaylistWithTracks
import com.example.myappplaylistmaker.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun deletePlaylist(playlistId: Int)

    @Transaction
    @Query("SELECT * FROM playlist_table ORDER BY timestamp DESC")
    fun getPlaylistWithTracks(): Flow<List<PlaylistWithTracks>>

    @Query("UPDATE playlist_table SET amountOfTracks = amountOfTracks + 1 WHERE playlistId = :playlistId")
    suspend fun updatePlaylist(playlistId: Int)

    @Transaction
    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId")
    fun getPlaylistById(playlistId: Int): Flow<PlaylistEntity>

    @Update
    suspend fun updatePlaylistInfo(playlist: PlaylistEntity): Int
}
