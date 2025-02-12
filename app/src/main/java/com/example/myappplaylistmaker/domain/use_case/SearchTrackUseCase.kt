package com.example.myappplaylistmaker.domain.use_case

import com.example.myappplaylistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface SearchTrackUseCase {
    fun searchTracks(artistOrSongName: String): Flow<Pair<List<Track>?, String?>>
}