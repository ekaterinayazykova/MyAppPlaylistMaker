package com.example.myappplaylistmaker.domain.repository

interface ThemeManagerRepository {
    fun getThemeModeState(): Boolean
    fun setNightModeState(setNightMode: Boolean)
}