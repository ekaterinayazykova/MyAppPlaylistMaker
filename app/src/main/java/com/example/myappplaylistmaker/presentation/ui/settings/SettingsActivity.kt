package com.example.myappplaylistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.example.myappplaylistmaker.core.Creator
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.databinding.ActivitySettingsBinding
import com.example.myappplaylistmaker.domain.interactor.ThemeManagerInteractor
import com.example.myappplaylistmaker.domain.use_case.SettingsOptionsUseCase
import com.example.myappplaylistmaker.presentation.view_models.settings.SettingsViewModel
import com.example.myappplaylistmaker.presentation.view_models.settings.SettingsViewModelFactory

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeManagerInteractor: ThemeManagerInteractor
    private lateinit var settingsOptionsUseCase: SettingsOptionsUseCase
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        themeManagerInteractor = Creator.createThemeManagerInteractor(this)
        settingsOptionsUseCase = Creator.createSettingsOptionsUseCase(this)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settingsViewModel = ViewModelProvider(this, SettingsViewModelFactory(themeManagerInteractor, settingsOptionsUseCase)).get(SettingsViewModel::class.java)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        setupThemeSwitch()



//        AppCompatDelegate.setDefaultNightMode(
//            if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
//        )
//        val switchNightMode = findViewById<SwitchCompat>(R.id.switchTheme)
//        switchNightMode.isChecked = isNightMode
//
//        switchNightMode.setOnCheckedChangeListener { _, isChecked ->
//            themeManagerInteractor.setNightModeState(isChecked)
//            recreate()
//        }
//        val isNightMode = themeManagerInteractor.getThemeModeState()


//        settingsViewModel.state.observe(this) { isNightMode ->
//            AppCompatDelegate.setDefaultNightMode(
//                if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
//            )
//            binding.switchTheme.isChecked = isNightMode
//        }
//        binding.switchTheme.setOnCheckedChangeListener {_, isChecked ->
//            settingsViewModel.setNightModeState(isChecked)
//            recreate()
//        }
//        settingsViewModel.state.value?.let {
//            binding.switchTheme.isChecked = it
//        }




        findViewById<ImageView>(R.id.arrow).setOnClickListener {
            finish()
        }

        val buttonShare = findViewById<Button>(R.id.share)
        buttonShare.setOnClickListener {
            shareApp()
        }

        val buttonSupport = findViewById<Button>(R.id.support)
        buttonSupport.setOnClickListener {
            writeSupport()
        }

        val buttonAcception = findViewById<Button>(R.id.acception)
        buttonAcception.setOnClickListener {
            acceptTermsOfUse()
        }
    }

    private fun setupThemeSwitch() {

//            val switchTheme: SwitchCompat = binding.switchTheme


            settingsViewModel._themeState.observe(this) { isNightMode ->
                Log.e("SettingsActivity", "button clicked observe = $isNightMode")
//                switchTheme.isChecked = isNightMode
            }

            findViewById<SwitchCompat>(R.id.switchTheme).setOnCheckedChangeListener { _, isChecked ->
                Log.e("SettingsActivity", "button clicked = $isChecked")
                settingsViewModel.setNightModeState(isChecked)

            }
        }


    private fun shareApp() {
        val shareText = getString(R.string.app_link)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(shareIntent, getString(R.string.share))
        startActivity(chooser)
    }

    private fun writeSupport() {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subjectWriteSupport))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.textWriteSupport))
        }
            startActivity(supportIntent)
    }

    private fun acceptTermsOfUse() {
        val acceptionIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.acception_link)))
        startActivity(acceptionIntent)
    }
}