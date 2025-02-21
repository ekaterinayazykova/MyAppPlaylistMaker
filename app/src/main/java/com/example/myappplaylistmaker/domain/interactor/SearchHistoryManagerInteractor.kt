package com.example.myappplaylistmaker.domain.interactor

import com.example.myappplaylistmaker.domain.entity.Track

interface SearchHistoryManagerInteractor {
    fun saveToHistory(track: Track)
    fun getSearchHistorySync(): List<Track>
    suspend fun getSearchHistory(): List<Track>
    fun clearHistory()
    fun clearPrefs()
}