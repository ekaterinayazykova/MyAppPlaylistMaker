package com.example.myappplaylistmaker.domain.repository

import android.content.Intent

interface SettingsOptionsRepository {
    fun createShareIntent(shareMessage: String): Intent
    fun writeSupport()
    fun acceptTermsOfUse()
}