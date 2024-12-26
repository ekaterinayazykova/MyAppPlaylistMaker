package com.example.myappplaylistmaker.presentation.UI.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.myappplaylistmaker.core.Creator
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.domain.interactor.ThemeManagerInteractor
import com.example.myappplaylistmaker.presentation.UI.search.SearchActivity
import com.example.myappplaylistmaker.presentation.UI.settings.SettingsActivity
import com.example.myappplaylistmaker.presentation.UI.library.LibraryActivity

class MainActivity : AppCompatActivity() {

    private lateinit var themeManagerInteractor: ThemeManagerInteractor

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        themeManagerInteractor = Creator.createThemeManagerInteractor(this)
        val isNightMode = themeManagerInteractor.getThemeModeState()
        AppCompatDelegate.setDefaultNightMode(
            if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
            enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.search)
        buttonSearch.setOnClickListener {
            openSearch()
        }
        val buttonMedia = findViewById<Button>(R.id.media)
        buttonMedia.setOnClickListener {
            openLibrary()
        }

        val buttonSettings = findViewById<Button>(R.id.settings)
        buttonSettings.setOnClickListener{
            openSettings()
        }

    }

    private fun openSearch() {
        val searchActivity = Intent(this, SearchActivity:: class.java)
        startActivity(searchActivity)
    }

    private fun openLibrary() {
        val libraryActivity = Intent(this, LibraryActivity:: class.java)
        startActivity(libraryActivity)
    }

    private fun openSettings() {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }
}