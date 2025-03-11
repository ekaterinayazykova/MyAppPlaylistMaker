package com.example.myappplaylistmaker.data.db.cross_ref

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.myappplaylistmaker.data.db.entity.PlaylistEntity
import com.example.myappplaylistmaker.data.db.entity.PlaylistTrackCrossRef
import com.example.myappplaylistmaker.data.db.entity.TrackEntity

data class TrackWithPlaylists(
    @Embedded val track: TrackEntity,
    @Relation(
        parentColumn = "trackId",
        entityColumn = "playlistId",
        associateBy = Junction(PlaylistTrackCrossRef::class)
    )
    val playlists: List<PlaylistEntity>
)