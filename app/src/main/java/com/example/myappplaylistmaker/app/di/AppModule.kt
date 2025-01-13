package com.example.myappplaylistmaker.app.di

import com.example.myappplaylistmaker.app.presentation.view_models.media_player.MediaPlayerViewModel
import com.example.myappplaylistmaker.app.presentation.view_models.search.SearchViewModel
import com.example.myappplaylistmaker.app.presentation.view_models.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel<MediaPlayerViewModel>{
        MediaPlayerViewModel(mediaPlayerInteractor = get())
    }

    viewModel<SearchViewModel>{
        SearchViewModel(searchHistoryManager = get(),
                        searchTrackUseCase = get(),
                        stringProvider = get())
    }

    viewModel<SettingsViewModel>{
        SettingsViewModel(themeManagerInteractor = get(),
                          settingsOptionsUseCase = get())
    }
}