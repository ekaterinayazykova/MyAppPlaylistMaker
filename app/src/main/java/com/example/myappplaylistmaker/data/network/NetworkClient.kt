package com.example.myappplaylistmaker.data.network

import android.content.Context
import com.example.myappplaylistmaker.data.model.Response

interface NetworkClient {
    fun doRequest(artistOrSongName: String): Response
}