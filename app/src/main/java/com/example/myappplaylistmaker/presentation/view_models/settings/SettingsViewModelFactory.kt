package com.example.myappplaylistmaker.presentation.view_models.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myappplaylistmaker.domain.interactor.ThemeManagerInteractor
import com.example.myappplaylistmaker.domain.interactor.SettingsOptionsInteractor

class SettingsViewModelFactory (
    private val themeManagerInteractor: ThemeManagerInteractor,
    private val settingsOptionsUseCase: SettingsOptionsInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(themeManagerInteractor, settingsOptionsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}