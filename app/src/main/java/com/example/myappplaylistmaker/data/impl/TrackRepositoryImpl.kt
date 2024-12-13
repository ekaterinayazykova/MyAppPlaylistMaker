package com.example.myappplaylistmaker.data.impl

import android.util.Log
import com.example.myappplaylistmaker.data.converter.TrackConverter
import com.example.myappplaylistmaker.data.network.NetworkClient
import com.example.myappplaylistmaker.data.network.TrackResponse
import com.example.myappplaylistmaker.domain.entity.Resource
import com.example.myappplaylistmaker.domain.repository.TrackRepository
import com.example.myappplaylistmaker.domain.entity.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override fun searchTracks(artistOrSongName: String): Resource<List<Track>> {
        Log.d("TrackRepository", "Отправка запроса: $artistOrSongName")
        return try {
        val response = networkClient.doRequest(artistOrSongName)

        if (response.resultCode == 200) {
            val tracks = (response as TrackResponse).results.map { TrackConverter.map(it) }
            return Resource.Success(tracks)
        } else {
            return Resource.Error("Произошла сетевая ошибка")
        }
    } catch (e: Exception) {
            Log.e("TrackRepository", "Ошибка при выполнении запроса", e)
            Resource.Error("Произошла ошибка: ${e.message}")
        }
    }
}