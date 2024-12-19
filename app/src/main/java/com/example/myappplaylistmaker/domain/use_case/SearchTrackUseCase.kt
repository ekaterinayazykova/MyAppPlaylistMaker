package com.example.myappplaylistmaker.domain.use_case

import com.example.myappplaylistmaker.domain.consumer.Consumer
import com.example.myappplaylistmaker.domain.entity.Track

interface SearchTrackUseCase {
    fun execute(artistOrSongName: String, consumer: Consumer<List<Track>>)
    fun shutDown()
}