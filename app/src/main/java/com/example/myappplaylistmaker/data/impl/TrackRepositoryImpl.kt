package com.example.myappplaylistmaker.data.impl

import android.content.Context
import android.util.Log
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.data.converter.TrackConverter
import com.example.myappplaylistmaker.data.model.Response
import com.example.myappplaylistmaker.data.network.NetworkClient
import com.example.myappplaylistmaker.data.network.TrackResponse
import com.example.myappplaylistmaker.data.utils.StringProvider
import com.example.myappplaylistmaker.domain.entity.Resource
import com.example.myappplaylistmaker.domain.repository.TrackRepository
import com.example.myappplaylistmaker.domain.entity.Track

class TrackRepositoryImpl(private val stringProvider: StringProvider, private val networkClient: NetworkClient) : TrackRepository {

    override fun searchTracks(artistOrSongName: String): Resource<List<Track>> {
        return try {
        val response = networkClient.doRequest(artistOrSongName)
        return when (response.resultCode) {
            Log.d("TrackRep", "code = ${response.resultCode}")
            -1 -> {
                Resource.Error(stringProvider.getString(R.string.no_internet))
            }
            200 -> {
                Resource.Success((response as TrackResponse).results.map { TrackConverter.map(it) })
            }
            else -> {
            Resource.Error(stringProvider.getString(R.string.network_error))
        }
    }
        }
        catch (e: Exception) {
            Resource.Error("${stringProvider.getString(R.string.error_occured)} ${e.message}")
        }
    }
}
