package com.example.myappplaylistmaker.presentation.history

import com.example.myappplaylistmaker.domain.entity.Track

sealed interface HistoryState {

    object Loading: HistoryState

    data class Content(
        val tracks: List<Track>
    ) : HistoryState

    data class Empty(
        val message: String
    ) : HistoryState
}