package com.example.myappplaylistmaker.domain.impl

import com.example.myappplaylistmaker.data.utils.StringProvider
import com.example.myappplaylistmaker.domain.entity.Resource
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.repository.TrackRepository
import com.example.myappplaylistmaker.domain.use_case.SearchTrackUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchTrackUseCaseImpl(
    private val stringProvider: StringProvider,
    private val repository: TrackRepository
) : SearchTrackUseCase {

    override fun searchTracks(artistOrSongName: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(artistOrSongName).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}