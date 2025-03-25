package com.example.myappplaylistmaker.presentation.view_models.settings

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myappplaylistmaker.domain.interactor.ThemeManagerInteractor
import com.example.myappplaylistmaker.domain.interactor.SettingsOptionsInteractor

class SettingsViewModel (
    private val themeManagerInteractor: ThemeManagerInteractor,
    private val settingsOptionsUseCase: SettingsOptionsInteractor
) : ViewModel() {

    private val _themeState : MutableLiveData<Boolean> = MutableLiveData()
    val themeState: LiveData<Boolean> get() = this._themeState

    init {
        this._themeState.value = themeManagerInteractor.getThemeModeState()
    }

    fun setNightModeState(setNightMode: Boolean) {
        themeManagerInteractor.setNightModeState(setNightMode)
        this._themeState.value = setNightMode
    }

    fun createShareIntent(shareMessage: String): Intent {
        return settingsOptionsUseCase.createShareIntent(shareMessage)
    }

    fun writeSupport(){
        settingsOptionsUseCase.writeSupport()
    }

    fun acceptTermsOfUse(){
        settingsOptionsUseCase.acceptTermsOfUse()
    }
}