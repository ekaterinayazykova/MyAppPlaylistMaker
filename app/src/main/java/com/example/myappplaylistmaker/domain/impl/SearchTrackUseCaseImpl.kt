package com.example.myappplaylistmaker.domain.impl

import android.content.Context
import android.util.Log
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.data.utils.StringProvider
import com.example.myappplaylistmaker.domain.consumer.Consumer
import com.example.myappplaylistmaker.domain.consumer.ConsumerData
import com.example.myappplaylistmaker.domain.entity.Resource
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.repository.TrackRepository
import com.example.myappplaylistmaker.domain.use_case.SearchTrackUseCase
import java.util.concurrent.Executors

class SearchTrackUseCaseImpl (private val stringProvider: StringProvider, private val repository: TrackRepository) : SearchTrackUseCase {

    private val executor = Executors.newSingleThreadExecutor()

    override fun execute(artistOrSongName: String, consumer: Consumer<List<Track>>) {
        executor.execute {
            try {
                when (val tracks = repository.searchTracks(artistOrSongName)) {
                    is Resource.Success -> {
                        Log.d("SearchTrack", "tracks: $tracks")
                        consumer.consume(ConsumerData.Data(tracks.data))
                }
                is Resource.Error -> {
                    consumer.consume(ConsumerData.Error(tracks.message))
                }
            }
        } catch (e: Exception) {
                val errorMessage = stringProvider.getString(R.string.error_occured)
                consumer.consume(ConsumerData.Error("$errorMessage: ${e.message}"))
            }
        }
    }

    override fun shutDown() {
        executor.shutdown()
    }
}