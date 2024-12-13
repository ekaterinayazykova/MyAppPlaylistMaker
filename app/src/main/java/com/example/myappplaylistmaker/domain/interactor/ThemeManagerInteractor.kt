package com.example.myappplaylistmaker.domain.interactor

interface ThemeManagerInteractor {
    fun getThemeModeState(): Boolean
    fun setNightModeState(setNightMode: Boolean)
}