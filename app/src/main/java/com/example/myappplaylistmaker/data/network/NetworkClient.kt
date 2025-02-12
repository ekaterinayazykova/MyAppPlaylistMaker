package com.example.myappplaylistmaker.data.network

import android.content.Context
import com.example.myappplaylistmaker.data.model.Response

interface NetworkClient {
    suspend fun doRequest(artistOrSongName: Any): Response
}