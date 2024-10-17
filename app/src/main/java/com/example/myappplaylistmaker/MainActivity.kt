package com.example.myappplaylistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        ThemeManager.setNightModeState(this, ThemeManager.getNightModeState(this))

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