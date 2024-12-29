package com.example.myappplaylistmaker.presentation.ui.settings


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.myappplaylistmaker.core.App
import com.example.myappplaylistmaker.core.Creator
import com.example.myappplaylistmaker.databinding.ActivitySettingsBinding
import com.example.myappplaylistmaker.domain.interactor.ThemeManagerInteractor
import com.example.myappplaylistmaker.presentation.view_models.settings.SettingsViewModel
import com.example.myappplaylistmaker.presentation.view_models.settings.SettingsViewModelFactory

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val themeManagerInteractor: ThemeManagerInteractor
        get() = (application as App).themeManagerInteractor
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(
            themeManagerInteractor,
            Creator.createSettingsOptionsUseCase(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        bindingButtons()
        setupThemeSwitch()

    }

    private fun setupThemeSwitch() {
        val switchTheme: SwitchCompat = binding.switchTheme
        settingsViewModel._themeState.observe(this) { setNightMode ->
            switchTheme.isChecked = setNightMode
        }
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setNightModeState(isChecked)
        }

    }

    private fun bindingButtons() {
        binding.arrow.setOnClickListener {
            finish()
        }

        binding.share.setOnClickListener {
            settingsViewModel.shareApp()
        }

        binding.support.setOnClickListener {
            settingsViewModel.writeSupport()
        }

        binding.acception.setOnClickListener {
            settingsViewModel.acceptTermsOfUse()
        }
    }
}