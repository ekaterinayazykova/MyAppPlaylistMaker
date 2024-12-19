package com.example.myappplaylistmaker.data.network

import com.example.myappplaylistmaker.data.model.Response
import com.example.myappplaylistmaker.data.model.TrackDto

data class TrackResponse (
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()