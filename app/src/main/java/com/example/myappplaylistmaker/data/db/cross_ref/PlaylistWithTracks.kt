package com.example.myappplaylistmaker.data.db.cross_ref

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.myappplaylistmaker.data.db.entity.PlaylistEntity
import com.example.myappplaylistmaker.data.db.entity.PlaylistTrackCrossRef
import com.example.myappplaylistmaker.data.db.entity.TrackEntity

data class PlaylistWithTracks(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "trackId",
        associateBy = Junction(PlaylistTrackCrossRef::class)
    )
    val tracks: List<TrackEntity>
)
