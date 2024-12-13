package com.example.myappplaylistmaker.domain.repository

import com.example.myappplaylistmaker.domain.entity.Track

interface SearchHistoryManagerRepository {
    fun saveToHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearHistory()
}