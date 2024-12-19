package com.example.myappplaylistmaker.domain.impl

import com.example.myappplaylistmaker.domain.interactor.ThemeManagerInteractor
import com.example.myappplaylistmaker.domain.repository.ThemeManagerRepository

class ThemeManagerInteractorImpl (private val themeManagerRepository: ThemeManagerRepository) :
    ThemeManagerInteractor {
    override fun getThemeModeState(): Boolean {
        return themeManagerRepository.getThemeModeState()
    }

    override fun setNightModeState(setNightMode: Boolean) {
        return themeManagerRepository.setNightModeState(setNightMode)
    }

}