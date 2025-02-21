package com.example.myappplaylistmaker.data.impl

import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.data.converter.TrackConverter
import com.example.myappplaylistmaker.data.db.AppDatabase
import com.example.myappplaylistmaker.data.model.TrackRequest
import com.example.myappplaylistmaker.data.network.NetworkClient
import com.example.myappplaylistmaker.data.network.TrackResponse
import com.example.myappplaylistmaker.data.utils.StringProvider
import com.example.myappplaylistmaker.domain.entity.Resource
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val stringProvider: StringProvider,
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase,
) : TrackRepository {

    override fun searchTracks(artistOrSongName: String): Flow<Resource<List<Track>>> = flow {
        try {
            val response = networkClient.doRequest(TrackRequest(artistOrSongName))
            when (response.resultCode) {
                - 1 -> {
                    emit(Resource.Error(stringProvider.getString(R.string.no_internet)))
                }
                200 -> {
                    val resultTracks = (response as TrackResponse).results.map { TrackConverter.map(it) }

                    val favTrackId = appDatabase.trackDao().getTrackId()

                    val checkedTracks = resultTracks.map { track ->
                        track.copy(isFavorite = track.trackId in favTrackId)
                    }
                    emit(Resource.Success(checkedTracks))
                }
                else -> {
                    emit(Resource.Error(stringProvider.getString(R.string.network_error)))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("${stringProvider.getString(R.string.error_occured)} ${e.message}"))
        }
    }
}