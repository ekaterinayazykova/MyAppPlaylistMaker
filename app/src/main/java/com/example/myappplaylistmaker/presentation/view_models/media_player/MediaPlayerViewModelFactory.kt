package com.example.myappplaylistmaker.presentation.view_models.media_player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myappplaylistmaker.domain.interactor.MediaPlayerInteractor
import com.example.myappplaylistmaker.presentation.view_models.settings.SettingsViewModel

class MediaPlayerViewModelFactory (private val mediaPlayerInteractor: MediaPlayerInteractor): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaPlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MediaPlayerViewModel(mediaPlayerInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")

    }
}
