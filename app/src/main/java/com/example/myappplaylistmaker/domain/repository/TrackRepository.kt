package com.example.myappplaylistmaker.domain.repository

import com.example.myappplaylistmaker.domain.entity.Resource
import com.example.myappplaylistmaker.domain.entity.Track

interface TrackRepository {
    fun searchTracks(artistOrSongName: String ) : Resource<List<Track>>
}