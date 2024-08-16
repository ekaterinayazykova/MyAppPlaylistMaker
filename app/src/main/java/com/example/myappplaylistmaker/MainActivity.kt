package com.example.myappplaylistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
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

    fun openSearch() {
        val searchActivity = Intent(this, SearchActivity:: class.java)
        startActivity(searchActivity)
    }

    fun openLibrary() {
        val libraryActivity = Intent(this, LibraryActivity:: class.java)
        startActivity(libraryActivity)
    }

    fun openSettings() {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }

}