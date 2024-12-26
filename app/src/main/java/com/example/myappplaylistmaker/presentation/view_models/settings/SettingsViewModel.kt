package com.example.myappplaylistmaker.presentation.view_models.settings

import android.util.Log
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

    private val settingsOption = MutableLiveData<Option?>()
    val chooseSettingOption: LiveData<Option?> get() = settingsOption


    fun setNightModeState(setNightMode: Boolean) {
        Log.d("SettingsViewModel", "Setting night mode to: $setNightMode")
        themeManagerInteractor.setNightModeState(setNightMode)
        themeState.value = setNightMode

    }


    override fun onCleared() {
        Log.e("Settings", "Settings VM cleared")
        super.onCleared()
    }

    sealed class Option {
        object SHARE : Option()
        object SUPPORT : Option()
        object TERMS : Option()
    }

}