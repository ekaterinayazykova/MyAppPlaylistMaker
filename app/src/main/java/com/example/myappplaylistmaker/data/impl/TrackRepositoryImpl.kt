package com.example.myappplaylistmaker.data.impl

import android.content.Context
import android.provider.Settings.Secure.getString
import android.util.Log
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.data.converter.TrackConverter
import com.example.myappplaylistmaker.data.network.NetworkClient
import com.example.myappplaylistmaker.data.network.TrackResponse
import com.example.myappplaylistmaker.domain.entity.Resource
import com.example.myappplaylistmaker.domain.repository.TrackRepository
import com.example.myappplaylistmaker.domain.entity.Track

class TrackRepositoryImpl(private val context: Context, private val networkClient: NetworkClient) : TrackRepository {

    override fun searchTracks(artistOrSongName: String): Resource<List<Track>> {
        return try {
        val response = networkClient.doRequest(artistOrSongName)

        if (response.resultCode == 200) {
            val tracks = (response as TrackResponse).results.map { TrackConverter.map(it) }
            return Resource.Success(tracks)
        } else {
            return Resource.Error(context.getString(R.string.network_error))
        }
    } catch (e: Exception) {
            Resource.Error("${context.getString(R.string.error_occured)} ${e.message}")
        }
    }
}