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
        val state = sharedPreferences.getBoolean(THEME_KEY, false)
        Log.d("ThemeManager", "Retrieved theme mode state: $state")
        return state
    }

    override fun setNightModeState(setNightMode: Boolean) {
        Log.e("ThemeManager", "button clicked repository = $setNightMode")
        AppCompatDelegate.setDefaultNightMode(
            if (setNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, setNightMode)
            .apply()
        Log.d("ThemeManager", "Setting theme mode state: $setNightMode")
    }
}