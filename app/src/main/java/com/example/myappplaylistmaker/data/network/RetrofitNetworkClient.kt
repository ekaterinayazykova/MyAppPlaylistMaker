package com.example.myappplaylistmaker.data.network

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.myappplaylistmaker.data.model.Response
import com.example.myappplaylistmaker.presentation.utils.NetworkChecker
import com.example.myappplaylistmaker.presentation.utils.NetworkClass
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient (
    private val networkChecker: NetworkChecker,
    private val itunesService: ItunesApi) : NetworkClient {

//    private val retrofit = Retrofit.Builder()
//        .baseUrl(ITUNES_URL)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    private val api = retrofit.create(ItunesApi::class.java)

    @SuppressLint("SuspiciousIndentation")
    override fun doRequest(artistOrSongName: String): Response {

        return try {
            if (!networkChecker.isNetworkAvailable()) {
                return Response().apply { resultCode = -1 }
            } else {
                val response = itunesService.search(artistOrSongName).execute()
                val networkResponse = response.body() ?: Response()
                networkResponse.apply { resultCode = response.code() }
            }
        } catch (ex: Exception) {
            Response().apply { resultCode = 400 }
        }
    }
//    companion object {
//        private const val ITUNES_URL = "https://itunes.apple.com/"
//    }
}