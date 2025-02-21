package com.example.myappplaylistmaker.di

import com.example.myappplaylistmaker.presentation.view_models.media_library.FavTracksViewModel
import com.example.myappplaylistmaker.presentation.view_models.media_library.PlaylistViewModel
import com.example.myappplaylistmaker.presentation.view_models.media_player.MediaPlayerViewModel
import com.example.myappplaylistmaker.presentation.view_models.search.SearchViewModel
import com.example.myappplaylistmaker.presentation.view_models.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel<MediaPlayerViewModel>{
        MediaPlayerViewModel(mediaPlayerInteractor = get())
    }

    viewModel<SearchViewModel>{
        SearchViewModel(searchHistoryManager = get(),
                        searchTrackUseCase = get(),
                        stringProvider = get(),
                        savedStateHandle = get())
    }

    viewModel<SettingsViewModel>{
        SettingsViewModel(themeManagerInteractor = get(),
                          settingsOptionsUseCase = get())
    }

    viewModel<FavTracksViewModel>(){
        FavTracksViewModel()
    }

    viewModel<PlaylistViewModel>(){
        PlaylistViewModel()
    }
}