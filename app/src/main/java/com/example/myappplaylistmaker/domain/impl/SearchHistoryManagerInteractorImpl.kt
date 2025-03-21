package com.example.myappplaylistmaker.domain.impl

import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.SearchHistoryManagerInteractor
import com.example.myappplaylistmaker.domain.repository.SearchHistoryManagerRepository

class SearchHistoryManagerInteractorImpl (private val repository: SearchHistoryManagerRepository) : SearchHistoryManagerInteractor{
    override fun saveToHistory(track: Track) {
        return repository.saveToHistory(track)
    }

    override fun getSearchHistorySync(): List<Track> {
        return repository.getSearchHistorySync()
    }

    override suspend fun getSearchHistory(): List<Track> {
        return repository.getSearchHistory()
    }

    override fun clearHistory() {
        return repository.clearHistory()
    }

    override fun clearPrefs() {
        return repository.clearPrefs()
    }
}