package com.example.myappplaylistmaker.domain.impl

import android.content.Context
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.domain.consumer.Consumer
import com.example.myappplaylistmaker.domain.consumer.ConsumerData
import com.example.myappplaylistmaker.domain.entity.Resource
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.repository.TrackRepository
import com.example.myappplaylistmaker.domain.use_case.SearchTrackUseCase
import java.util.concurrent.Executors

class SearchTrackUseCaseImpl (private val context: Context, private val repository: TrackRepository) : SearchTrackUseCase {

    private val executor = Executors.newSingleThreadExecutor()

    override fun execute(artistOrSongName: String, consumer: Consumer<List<Track>>) {
        executor.execute {
            try {
                when (val tracks = repository.searchTracks(artistOrSongName)) {
                    is Resource.Success -> {
                        consumer.consume(ConsumerData.Data(tracks.data))
                }
                is Resource.Error -> {
                    consumer.consume(ConsumerData.Error(context.getString(R.string.goes_wrong)))
                }
            }
        } catch (e: Exception) {
                consumer.consume(ConsumerData.Error("${context.getString(R.string.error_occured)} ${e.message}"))
            }
        }
    }

    override fun shutDown() {
        executor.shutdown()
    }
}