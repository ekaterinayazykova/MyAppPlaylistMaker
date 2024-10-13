package com.example.myappplaylistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

        private const val THEME_PREFS = "theme_preferences"
        private const val THEME_KEY = "theme_key"

    fun getNightModeState(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean(THEME_KEY, false)
    }
    fun setNightModeState(context: Context, setNightMode: Boolean) {
        val sharedPrefs = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean(THEME_KEY, setNightMode).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (setNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}