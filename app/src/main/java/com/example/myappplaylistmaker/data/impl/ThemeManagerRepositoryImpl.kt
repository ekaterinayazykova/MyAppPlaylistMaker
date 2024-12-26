package com.example.myappplaylistmaker.data.impl

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.example.myappplaylistmaker.core.Constants.THEME_KEY
import com.example.myappplaylistmaker.core.Constants.THEME_PREFS
import com.example.myappplaylistmaker.domain.repository.ThemeManagerRepository

class ThemeManagerRepositoryImpl(context: Context) : ThemeManagerRepository {

    private val sharedPreferences =
        context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)


    override fun getThemeModeState(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }

    override fun setNightModeState(setNightMode: Boolean) {
        Log.e("SettingsActivity", "button clicked repository = $setNightMode")
        AppCompatDelegate.setDefaultNightMode(
            if (setNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, setNightMode)
            .apply()
    }
}