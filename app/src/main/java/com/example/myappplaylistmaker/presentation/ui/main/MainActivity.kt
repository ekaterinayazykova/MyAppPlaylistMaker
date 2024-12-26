package com.example.myappplaylistmaker.presentation.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.myappplaylistmaker.presentation.view_models.main.MainViewModel
import com.example.myappplaylistmaker.core.Creator
import com.example.myappplaylistmaker.databinding.ActivityMainBinding
import com.example.myappplaylistmaker.domain.interactor.ThemeManagerInteractor
import com.example.myappplaylistmaker.presentation.ui.search.SearchActivity
import com.example.myappplaylistmaker.presentation.ui.settings.SettingsActivity
import com.example.myappplaylistmaker.presentation.ui.library.LibraryActivity

class MainActivity : AppCompatActivity() {

    private lateinit var themeManagerInteractor: ThemeManagerInteractor
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        themeManagerInteractor = Creator.createThemeManagerInteractor(this)
        val isNightMode = themeManagerInteractor.getThemeModeState()
        AppCompatDelegate.setDefaultNightMode(
            if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
            enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e("AAA", "Activity created")

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding.search.setOnClickListener {
            viewModel.openSearch()
        }

        binding.media.setOnClickListener {
            viewModel.openLibrary()
        }

        binding.settings.setOnClickListener{
            viewModel.openSettings()
        }

        viewModel.chooseOption.observe(this) { menuOption ->
            menuOption?.let {
                when (it) {
                    MainViewModel.MenuOption.SEARCH -> {
                        startActivity(Intent(this, SearchActivity::class.java))
                    }
                    MainViewModel.MenuOption.LIBRARY -> {
                        startActivity(Intent(this, LibraryActivity::class.java))
                    }
                    MainViewModel.MenuOption.SETTINGS -> {
                        startActivity(Intent(this, SettingsActivity::class.java))
                    }
                }
            }
        }
    }



}