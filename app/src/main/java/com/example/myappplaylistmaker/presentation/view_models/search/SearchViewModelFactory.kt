package com.example.myappplaylistmaker.presentation.view_models.search

import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myappplaylistmaker.core.Creator

class SearchViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val searchTrackUseCase = Creator.provideTrackUseCase(context = context)
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val searchHistoryManagerInteractor = Creator.createSearchHistoryManagerInteractor(sharedPreferences)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(
            searchHistoryManager = searchHistoryManagerInteractor,
            searchTrackUseCase = searchTrackUseCase) as T
    }
}