package com.example.myappplaylistmaker.domain.repository

import com.example.myappplaylistmaker.domain.entity.Resource
import com.example.myappplaylistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(artistOrSongName: String ) : Flow<Resource<List<Track>>>
}