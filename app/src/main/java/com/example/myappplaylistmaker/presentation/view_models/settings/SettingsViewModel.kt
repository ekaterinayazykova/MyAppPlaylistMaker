package com.example.myappplaylistmaker.presentation.view_models.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myappplaylistmaker.domain.interactor.ThemeManagerInteractor
import com.example.myappplaylistmaker.domain.use_case.SettingsOptionsUseCase

class SettingsViewModel (
    private val themeManagerInteractor: ThemeManagerInteractor,
    private val settingsOptionsUseCase: SettingsOptionsUseCase
) : ViewModel() {

    private val themeState : MutableLiveData<Boolean> = MutableLiveData()
    val _themeState: LiveData<Boolean> get() = themeState

    init {
        themeState.value = themeManagerInteractor.getThemeModeState()
    }

    fun setNightModeState(setNightMode: Boolean) {
        themeManagerInteractor.setNightModeState(setNightMode)
        themeState.value = setNightMode
    }

    fun shareApp(){
        settingsOptionsUseCase.shareApp()
    }

    fun writeSupport(){
        settingsOptionsUseCase.writeSupport()
    }

    fun acceptTermsOfUse(){
        settingsOptionsUseCase.acceptTermsOfUse()
    }
}