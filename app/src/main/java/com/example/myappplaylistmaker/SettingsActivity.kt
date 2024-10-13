package com.example.myappplaylistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources.Theme
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_settings)

        val switchNightMode = findViewById<SwitchCompat>(R.id.switchTheme)
        switchNightMode.isChecked = ThemeManager.getNightModeState(this)

        switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            ThemeManager.setNightModeState(this, isChecked)
        }

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