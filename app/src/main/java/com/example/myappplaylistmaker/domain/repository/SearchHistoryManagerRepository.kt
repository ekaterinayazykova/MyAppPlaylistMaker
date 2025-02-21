package com.example.myappplaylistmaker.domain.repository

import com.example.myappplaylistmaker.domain.entity.Track

interface SearchHistoryManagerRepository {
    fun saveToHistory(track: Track)
    suspend fun getSearchHistory(): List<Track>
    fun getSearchHistorySync(): List<Track>
    fun clearHistory()
    fun clearPrefs()
}