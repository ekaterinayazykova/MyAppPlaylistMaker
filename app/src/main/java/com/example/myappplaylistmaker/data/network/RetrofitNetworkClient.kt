package com.example.myappplaylistmaker.data.network

import android.annotation.SuppressLint
import com.example.myappplaylistmaker.data.model.Response
import com.example.myappplaylistmaker.data.model.TrackRequest
import com.example.myappplaylistmaker.presentation.utils.NetworkChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient (
    private val networkChecker: NetworkChecker,
    private val itunesService: ItunesApi) : NetworkClient {

    @SuppressLint("SuspiciousIndentation")
    override suspend fun doRequest(artistOrSongName: Any): Response {

        if (!networkChecker.isNetworkAvailable()) {
            return Response().apply { resultCode = -1 }
        }
        if (artistOrSongName !is TrackRequest) {
            return Response().apply { resultCode = 400 }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = itunesService.search(artistOrSongName.artistOrSongName)
                response.apply { resultCode = 200 }
            } catch (e: Throwable) {
                Response().apply { resultCode = 500 }

            }
        }
    }
}