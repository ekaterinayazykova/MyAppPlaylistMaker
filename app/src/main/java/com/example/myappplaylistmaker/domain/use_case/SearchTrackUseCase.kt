package com.example.myappplaylistmaker.domain.use_case

import android.util.Log
import com.example.myappplaylistmaker.domain.consumer.Consumer
import com.example.myappplaylistmaker.domain.consumer.ConsumerData
import com.example.myappplaylistmaker.domain.entity.Resource
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.repository.TrackRepository
import java.util.concurrent.Executors

class SearchTrackUseCase (private val repository: TrackRepository) {

    private val executor = Executors.newSingleThreadExecutor()

    fun execute(artistOrSongName: String, consumer: Consumer<List<Track>>) {
        executor.execute {
            try {
                Log.d("SearchTrackUseCase", "Запуск поиска для $artistOrSongName")
                when (val tracks = repository.searchTracks(artistOrSongName)) {
                    is Resource.Success -> {
                        Log.d("SearchTrackUseCase", "Успех: получено ${tracks.data.size} треков")
                        consumer.consume(ConsumerData.Data(tracks.data))
                }
                is Resource.Error -> {
                    Log.e("SearchTrackUseCase", "Ошибка: ${tracks.message}")
                    consumer.consume(ConsumerData.Error("Что-то пошло не так"))
                }
            }
        } catch (e: Exception) {
                Log.e("SearchTrackUseCase", "Ошибка во время выполнения запроса", e)
                consumer.consume(ConsumerData.Error("Произошла ошибка: ${e.message}"))
            }
        }
    }
    fun shutdown() {
        executor.shutdown()
    }
}