package com.example.myappplaylistmaker.data.impl

import android.content.Context
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.data.converter.TrackConverter
import com.example.myappplaylistmaker.data.model.Response
import com.example.myappplaylistmaker.data.network.NetworkClient
import com.example.myappplaylistmaker.data.network.TrackResponse
import com.example.myappplaylistmaker.domain.entity.Resource
import com.example.myappplaylistmaker.domain.repository.TrackRepository
import com.example.myappplaylistmaker.domain.entity.Track

class TrackRepositoryImpl(private val context: Context, private val networkClient: NetworkClient) : TrackRepository {

    override fun searchTracks(artistOrSongName: String): Resource<List<Track>> {
        return try {
        val response = networkClient.doRequest(context,artistOrSongName)
        return when (response.resultCode) {
            -1 -> {
                Resource.Error(context.getString(R.string.no_internet))
            }
            200 -> {
                Resource.Success((response as TrackResponse).results.map { TrackConverter.map(it) })
            }
            else -> {
            Resource.Error(context.getString(R.string.network_error))
        }
    }
        }
        catch (e: Exception) {
            Resource.Error("${context.getString(R.string.error_occured)} ${e.message}")
        }
    }
}
