package com.example.myappplaylistmaker.core

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import com.example.myappplaylistmaker.domain.interactor.ThemeManagerInteractor
import com.example.myappplaylistmaker.presentation.ui.main.MainActivity

class App : Application() {
    lateinit var themeManagerInteractor: ThemeManagerInteractor

    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        themeManagerInteractor = Creator.createThemeManagerInteractor(this)

        val isNightMode = themeManagerInteractor.getThemeModeState()
        AppCompatDelegate.setDefaultNightMode(
            if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
}