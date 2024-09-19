package com.example.myappplaylistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("search")
    fun search(@Query("term") text: String): Call<TrackResponse>
}