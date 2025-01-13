package com.example.myappplaylistmaker.presentation.view_models.search

import android.preference.PreferenceManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.core.Creator
import com.example.myappplaylistmaker.data.utils.StringProvider
import com.example.myappplaylistmaker.domain.interactor.SearchHistoryManagerInteractor
import com.example.myappplaylistmaker.domain.use_case.SearchTrackUseCase

class SearchViewModelFactory(
    private val searchHistoryManager: SearchHistoryManagerInteractor,
    private val searchTrackUseCase: SearchTrackUseCase,
    private val stringProvider: StringProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchHistoryManager, searchTrackUseCase, stringProvider) as T
        }
        throw IllegalArgumentException(stringProvider.getString(R.string.error_occured))
    }
}