package com.example.myappplaylistmaker.data.network

import android.annotation.SuppressLint
import com.example.myappplaylistmaker.data.model.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ItunesApi::class.java)

    @SuppressLint("SuspiciousIndentation")
    override fun doRequest(artistOrSongName: String): Response {

        return try {
        val response = api.search(artistOrSongName).execute()
        val networkResponse = response.body() ?: Response()

            networkResponse.apply { resultCode = response.code() }
        } catch (ex: Exception) {
            Response().apply { resultCode = 400 }
        }
    }
    companion object {
        private const val ITUNES_URL = "https://itunes.apple.com/"
    }
}